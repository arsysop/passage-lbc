package ru.arsysop.passage.lbc.base.condition;

import java.util.TimerTask;

import ru.arsysop.passage.lic.net.TimeConditions;

public abstract class ConditionTimerTask extends TimerTask {

	private boolean isStopped = false;
	private String timeToLive = "";

	public ConditionTimerTask(String timeToLive) {
		this.timeToLive = timeToLive;
	}

	@Override
	public void run() {
		if (isStopped) {
			return;
		}
		if (TimeConditions.isFutureLocalDateTime(timeToLive)) {
			isStopped = true;
			timeExpired();
		}
	}

	public void stopTask() {
		isStopped = true;
	}

	abstract void timeExpired();

}
