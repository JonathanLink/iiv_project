package app.listener;

import app.views.objects.texts.AnimatedTextPlate;

public interface AnimatedTextListener {
	void animatedTextHasFinished(AnimatedTextPlate animatedTextPlate);
	void animatedTextHasFinishedHalfWay(AnimatedTextPlate animatedTextPlate);
}
