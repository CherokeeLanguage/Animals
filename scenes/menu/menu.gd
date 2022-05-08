extends Control

var button_ix: int = 0
var max_buttons: int = 5

onready var buttons: Dictionary = {
	0: $VerticalLayout/CenteredMenu/VBoxContainer/PlayButton,
	1: $VerticalLayout/CenteredMenu/VBoxContainer/Instructions,
	2: $VerticalLayout/CenteredMenu/VBoxContainer/Options,
	3: $VerticalLayout/CenteredMenu/VBoxContainer/About,
	4: $VerticalLayout/CenteredMenu/VBoxContainer/ExitButton
}

onready var blank_icon = load("res://assets/sprites/menu_select_blank-96.png")
onready var selected_icon = load("res://assets/sprites/menu_select-96.png")

func _ready() -> void:
	set_active_button()
	
func set_active_button() -> void:
	for btn in buttons.values():
		btn.icon = blank_icon
	buttons[button_ix].icon = selected_icon

func _process(delta: float) -> void:
	var prev_button_ix: int = button_ix
	if Input.is_action_just_pressed("ui_accept"):
		buttons[button_ix].emit_signal("pressed")
		return
	if Input.is_action_just_pressed("ui_down"):
		button_ix = (button_ix+1) % max_buttons
	if Input.is_action_just_pressed("ui_up"):
		button_ix = (button_ix-1+max_buttons) % max_buttons
	if Input.is_action_just_pressed("pause"):
		if button_ix == max_buttons - 1:
			buttons[button_ix].emit_signal("pressed")
			return
		button_ix = max_buttons - 1
	if prev_button_ix != button_ix:
		EffectAudio.play_audio(EffectAudio.FX.MENU_ITEM)
		set_active_button()

func _on_PlayButton_pressed() -> void:
	var params = {show_progress_bar = false}
	Game.change_scene("res://scenes/gameplay/gameplay.tscn", params)


func _on_ExitButton_pressed() -> void:
	$AudioStreamPlayer.stop()
	# gently shutdown the game
	var main = Game.main
	main.transitions.fade_in({
		'show_progress_bar': false
	})
	yield(main.transitions.anim, "animation_finished")
	yield(get_tree().create_timer(0.3), "timeout")
	get_tree().quit()


func _on_About_pressed() -> void:
	Game.change_scene("res://scenes/menu/about_scene.tscn")


func _on_Options_pressed() -> void:
	Game.change_scene("res://scenes/menu/options_scene.tscn")


func _on_Instructions_pressed() -> void:
	Game.change_scene("res://scenes/menu/instructions_scene.tscn")
