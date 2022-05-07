extends Control


func _ready() -> void:
	pass


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
