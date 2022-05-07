#!/usr/bin/env bash
"""true" '''\'
set -e
eval "$(conda shell.bash hook)"
conda deactivate
conda activate Animals
exec python "$0" "$@"
exit $?
''"""
import glob
import os
import re
import sys
from shutil import rmtree
import unicodedata as ud
from PIL import Image
from PIL import ImageOps
from pydub import AudioSegment
from pydub.effects import normalize
from tqdm import tqdm


def get_mp3s(base_dir: str) -> list[str]:
    mp3s = []
    for mp3 in glob.glob("*/*.mp3"):
        mp3s.append(os.path.basename(mp3))
    mp3s.sort()
    return mp3s


def get_png_lookup(base_dir: str) -> dict[str, list[str]]:
    lookup: dict[str, list[str]] = dict()
    pronunciations = get_pronounce_lookup(base_dir)
    for syllabary in pronunciations.keys():
        lookup[syllabary] = list()
        filename = pronunciations[syllabary][1].replace(":", "").replace(" ", "_")
        pngs = glob.glob(os.path.join(base_dir, "pictures", f"{filename}-*.png"))
        for png in pngs:
            lookup[syllabary].append(os.path.basename(png))
        lookup[syllabary].sort()
    return lookup


def get_pronounce_lookup(base_dir: str) -> dict[str, tuple[str, str]]:
    filename: str = "animals-game-mco.txt"
    lookup: dict[str, type[str, str]] = {}
    with open(os.path.join(base_dir, filename), "r") as f:
        line: str
        ix: int = 0
        for line in f:
            line = line.strip()
            if not line:
                continue
            fields = line.split("|")
            if len(fields) < 3:
                continue
            syllabary: str = fields[0].replace(".", "").strip()
            pron: str = fields[1].replace(".", "").strip()
            latin: str = re.sub("[^a-z ]", "", ud.normalize("NFD", pron))
            lookup[syllabary] = (pron, latin)
        print(f"Read {ix:,} pronunciation entries.")
    return lookup


def get_mp3_lookup(base_dir: str) -> dict[str, list[str]]:
    filename: str = "animals-game-mco.txt"
    syl: dict[str, list[str]] = {}
    lookup: dict[str, str] = {}
    with open(os.path.join(base_dir, "..", filename), "r") as f:
        line: str
        ix: int = 0
        dupe_check: set[str] = set()
        for line in f:
            line = line.strip()
            if not line:
                continue
            fields = line.split("|")
            if len(fields) < 3:
                continue

            syllabary: str = fields[0].replace(".", "").strip()
            pron: str = fields[1].replace(".", "").strip()
            latin: str = re.sub("[^a-z :]", "", ud.normalize("NFD", pron))
            if syllabary not in syl.keys():
                syl[syllabary] = list()
            if syllabary in dupe_check:
                print(f"DUPLICATE SYLLABARY: {line}")
            dupe_check.add(syllabary)
            mp3: str = fields[2]
            lookup[mp3] = syllabary
            ix += 1
        print(f"Read {ix:,} mp3 entries.")

    for mp3 in glob.glob(os.path.join(base_dir, "*/*.mp3")):
        filename = os.path.basename(mp3)
        if filename in lookup:
            syllabary = lookup[filename]
            syl[syllabary].append(mp3)
        ix += 1
    print(f"Lookup created for {ix:,d} mp3 files.")
    return syl


def main():
    base_dir: str = os.path.dirname(os.path.realpath(__file__))
    godot_assets: str = os.path.join(base_dir, "..", "..", "assets")
    godot_pictures: str = os.path.join(godot_assets, "challenges", "images")
    godot_audio: str = os.path.join(godot_assets, "challenges", "audio")
    godot_classes: str = os.path.join("..", "..", "classes")
    dest_script: str = os.path.join(godot_classes, "ChallengeData.gd")

    mp3s: dict[str, list[str]] = get_mp3_lookup(os.path.join(base_dir, "audio"))

    print("Remove previous audio")
    rmtree(godot_audio, ignore_errors=True)
    os.makedirs(godot_audio, exist_ok=True)

    print("Remove previous images")
    rmtree(godot_pictures, ignore_errors=True)
    os.makedirs(godot_pictures, exist_ok=True)

    os.makedirs(godot_classes, exist_ok=True)

    print(f"Processing images.")
    target_size: tuple[int, int] = (256, 256)
    pngs = get_png_lookup(base_dir)
    for syllabary in tqdm(pngs, file=sys.stdout):
        png_list = pngs[syllabary]
        for png_file in png_list:
            png_file = os.path.join(base_dir, "pictures", png_file)
            dest_image: str = os.path.join(godot_pictures, os.path.basename(png_file))
            png: Image.Image = Image.open(png_file).convert("RGBA")
            png = png.crop(png.getbbox())
            background: Image.Image = Image.new("RGBA", png.size, "#ffffffff")
            background.alpha_composite(png)
            png = ImageOps.pad(background, target_size, method=Image.LANCZOS, color="#ffffffff", centering=(0.5, 0.5))
            png.save(dest_image)

    print(f"Processing audio.")
    for mp3_key in tqdm(mp3s.keys(), file=sys.stdout):
        for mp3 in mp3s[mp3_key]:
            dir_name: str = os.path.basename(os.path.dirname(mp3))
            filename: str = os.path.basename(mp3)
            audio: AudioSegment = AudioSegment.from_file(mp3)
            audio = normalize(audio)
            output_file = os.path.join(godot_audio, f"{dir_name}-{filename}")
            audio.export(output_file, parameters=["-qscale:a", "3"])
            # shutil.copy2(mp3, os.path.join(godot_audio, f"{dir_name}-{filename}"))

    pronunciations = get_pronounce_lookup(base_dir)
    pngs = get_png_lookup(base_dir)

    for syllabary in [*pngs.keys()].copy():
        if syllabary not in mp3s:
            del pngs[syllabary]
            continue
        if syllabary not in pronunciations:
            del pngs[syllabary]
            continue
    for syllabary in [*mp3s.keys()].copy():
        if syllabary not in pngs:
            del mp3s[syllabary]

    with open(dest_script, "w") as f:
        f.write("extends Node\n")
        f.write("\n")
        f.write("class_name ChallengeData")
        f.write("\n")
        f.write("\n")
        f.write(f"# GENERATED BY {os.path.basename(__file__)}.\n")
        f.write("# DO NOT HAND EDIT THIS FILE.\n")
        f.write(f"# {len(mp3s):,} Entries")
        f.write("\n")
        f.write("\n")

        f.write("const pronunciations: Dictionary = {\n")
        first_pronunciation: bool = True
        for syllabary, pronunciation in pronunciations.items():
            if first_pronunciation:
                first_pronunciation = False
            else:
                f.write(",\n")
            f.write("    ")
            f.write("\""+syllabary+"\": ")
            f.write("\"")
            f.write(pronunciation[0])
            f.write("\"")
        f.write("\n}\n")
        f.write("\n")

        f.write("const pngs: Dictionary = {\n")
        first_lookup: bool = True
        for syllabary in pngs.keys():
            if first_lookup:
                first_lookup = False
            else:
                f.write(",\n")
            f.write("    ")
            f.write("\"")
            f.write(syllabary)
            f.write("\"")
            f.write(": [")
            first_png: bool = True
            for png_file in pngs[syllabary]:
                if first_png:
                    first_png = False
                else:
                    f.write(", ")
                f.write("\"")
                f.write(png_file)
                f.write("\"")
            f.write("]")
        f.write("\n}\n")
        f.write("\n")

        f.write("const latin: Dictionary = {\n")
        first_pronunciation: bool = True
        for syllabary, pronunciation in pronunciations.items():
            if first_pronunciation:
                first_pronunciation = False
            else:
                f.write(",\n")
            f.write("    ")
            f.write("\"" + syllabary + "\": ")
            f.write("\"")
            f.write(pronunciation[1])
            f.write("\"")
        f.write("\n}\n")
        f.write("\n")

        f.write("const mp3s: Dictionary = {\n")
        first_challenge: bool = True
        for challenge in mp3s.keys():
            if first_challenge:
                first_challenge = False
            else:
                f.write(",\n")
            f.write("    ")
            entries = mp3s[challenge]
            f.write("\""+challenge+"\""+": [")
            first_entry: bool = True
            for entry in entries:
                dir_name: str = os.path.basename(os.path.dirname(entry))
                filename: str = os.path.basename(entry)
                entry = f"{dir_name}-{filename}"
                if first_entry:
                    first_entry = False
                else:
                    f.write(", ")
                f.write("\""+entry+"\"")
            f.write("]")
        f.write("\n}\n")
        f.write("\n")
        f.write("# EOF\n")


if __name__ == "__main__":
    main()
