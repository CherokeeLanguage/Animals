extends Control

class_name about_scene

onready var text_box = $VBoxContainer/ScrollContainer/Label

func _ready() -> void:
	var about_text: String = load_text("res://text/credits.txt")
	text_box.text = about_text.strip_edges() + "\n"

func load_text(text_file: String) -> String:
	var file: File = File.new()
	file.open(text_file, File.READ)
	var content: String = file.get_as_text()
	file.close()
	return content

func _on_Back_pressed() -> void:
	Game.change_scene("res://scenes/menu/menu.tscn", {
		'show_progress_bar': false
	})

func _input(event: InputEvent) -> void:
	if event.is_action_pressed("pause"):
		_on_Back_pressed()
	if event is InputEventMouseMotion:
		return
	print(event.as_text())
