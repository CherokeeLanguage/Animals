extends SmartAudioStream

func play_error():
	var ix: int = randi() % self.samples.size
	play_audio(ix)
	
