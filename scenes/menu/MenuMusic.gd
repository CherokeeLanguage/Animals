extends AudioStreamPlayer

func _on_AudioStreamPlayer_finished() -> void:
	yield(get_tree().create_timer(15), "timeout")
	play()
