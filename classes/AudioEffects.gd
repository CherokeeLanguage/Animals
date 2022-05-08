extends AudioStreamPlayer

# class_name AudioEffects

var wrong: Array = Array()
var end_round: Resource
var start_round: Resource
var menu_sound: Resource

func _ready() -> void:
	self.bus = "Effects"
	wrong.append(load("res://assets/audio-effects/bark.mp3"))
	wrong.append(load("res://assets/audio-effects/buzzer2.mp3"))
	wrong.append(load("res://assets/audio-effects/dialogerror.mp3"))
	wrong.append(load("res://assets/audio-effects/whip_pop.mp3"))
	end_round = load("res://assets/audio-effects/cash_out.mp3")
	start_round = load("res://assets/audio-effects/ding-ding-ding.mp3")
	menu_sound = load("res://assets/audio-effects/box_moved.mp3")

func play_effect(effect: Resource) -> void:
	self.stream = effect
	self.play()

func play_wrong() -> void:
	var ix = randi() % wrong.size()
	self.stream = wrong[ix]
	self.play()

