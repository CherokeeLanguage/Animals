extends "res://ui-nodes/UI_Button.gd"

func _ready() -> void:
	._ready()
	if OS.has_feature('HTML5'):
		queue_free()

