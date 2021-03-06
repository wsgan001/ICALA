package tdam.evaluation;

import input.SenderUDP;

import java.util.ArrayList;
import java.util.LinkedList;

public class ItemPresenterMultipleClear extends SenderUDP {

	private LinkedList<String> itemsList;

	private LinkedList<Long> timesList;

	private int index;

	private long timeFinish;

	private long timeAfterFinish;

	public final static int ASSUMED_CYCLE_TIME = 100;

	public ItemPresenterMultipleClear() {
		super("ItemPresenter");
		itemsList = new LinkedList<String>();
		timesList = new LinkedList<Long>();
		index = 0;
		timeFinish = 0;
		timeAfterFinish = 0;
		System.out.println("Assuming a cycle time of " + ASSUMED_CYCLE_TIME + " ms");
	}

	public void addItem(String item, long time) {
		itemsList.add(item);
		timesList.add(time);
	}

	public void addBreak(long time) {
		addItem("", time);
	}

	public void addCommand(String command) {
		addItem("COMMAND " + command, 0);
	}

	@Override
	protected LinkedList<byte[]> readInput() {

		// check for end of list
		if (index == itemsList.size()) {
			//System.out.println("Finished");
			//System.exit(0);
			System.out.println("Time after Finish: " + timeAfterFinish + " ms");
			itemsList.add("");
			timesList.add(new Long(ASSUMED_CYCLE_TIME));
			timeAfterFinish += ASSUMED_CYCLE_TIME;
		}

		// fetch item
		String items = itemsList.get(index);
		Long time = timesList.get(index);

		// set time
		if (timeFinish == 0) {
			timeFinish = System.currentTimeMillis() + time.longValue();
			if (items.equals(""))
				System.out.println("No input for " + time + " ms (" + (time / ASSUMED_CYCLE_TIME) + " cycles)...");
			else if (items.startsWith("COMMAND"))
				System.out.println("Sending command: " + items);
			else
				System.out.println("Presenting items " + items + " for " + time + " ms (" + (time / ASSUMED_CYCLE_TIME) + " cycles)...");
		}

		// prepare inputs
		LinkedList<byte[]> inputs = new LinkedList<byte[]>();
		if (items.startsWith("COMMAND")) {
			inputs.add(items.getBytes());
		} else {
			for (int i = 0; i < items.length(); i++) {
				String inputChar = items.substring(i, i + 1) + "\n";
				inputs.add(inputChar.getBytes());
			}
		}

		// check for finish time
		long timeCurrent = System.currentTimeMillis();
		if (timeCurrent >= timeFinish) {
			index += 1;
			timeFinish = 0;
		}

		return inputs;
	}

	public static void main(String[] args) {
		ItemPresenterMultipleClear itemPresenter = new ItemPresenterMultipleClear();

		ArrayList<String[]> sequences = new ArrayList<String[]>();
		String[] sequence2 = { "1A", "2B", "3C", "4D", "5E" };//, "6F", "7G", "8H", "9I", "0J" };
		sequences.add(sequence2);
		String[] sequence3 = { "1AB", "2CD", "3EF", "4GH", "5IJ" };//, "6KL", "7MN", "8OP", "9QR", "0ST" };
		sequences.add(sequence3);
		String[] sequence4 = { "1ABC", "2DEF", "3GHI", "4JKL", "5MNO" };//, "6PQR", "7STU", "8VWX", "9YZ!", "0(*)" };
		sequences.add(sequence4);

		int cyclesItem = 10;
		int cyclesGap = 2;
		int repetitions = 10;
		int cyclesCue = 0;

		for (String[] sequence : sequences) {

			// info
			itemPresenter.addCommand("PRINT MESSAGE Learning each item for " + cyclesItem + " cycles");
			itemPresenter.addCommand("PRINT MESSAGE Learning with gap of " + cyclesGap + " cycles");
			itemPresenter.addCommand("PRINT MESSAGE Learning the sequence " + repetitions + " times");
			itemPresenter.addCommand("PRINT MESSAGE Learning a sequence of " + sequence.length + " items");

			// initialise
			itemPresenter.addBreak(100 * ASSUMED_CYCLE_TIME);

			// log
			itemPresenter.addCommand("PRINT MESSAGE Learning starts");

			for (int repetition = 1; repetition <= repetitions; repetition++) {

				// log
				itemPresenter.addCommand("PRINT MESSAGE Repetition " + repetition + " of " + repetitions);

				for (String item : sequence) {

					// item
					itemPresenter.addItem(item, cyclesItem * ASSUMED_CYCLE_TIME);

					// gap
					itemPresenter.addBreak(cyclesGap * ASSUMED_CYCLE_TIME);

				}

				// load decay
				itemPresenter.addBreak(100 * ASSUMED_CYCLE_TIME);

			}

			// log
			itemPresenter.addCommand("PRINT MESSAGE Learning finished");

			// measure weights
			itemPresenter.addCommand("PRINT ASSOCIATIONS");

			// activation decay
			itemPresenter.addBreak(100 * ASSUMED_CYCLE_TIME);
			itemPresenter.addCommand("PRINT MESSAGE Decayed activation after 100 cycles:");
			itemPresenter.addCommand("PRINT UNITS");

			// log
			itemPresenter.addCommand("PRINT MESSAGE Recall starts");

			// recall
			itemPresenter.addCommand("PRINT MESSAGE Recall Phase: Presenting cue A for " + cyclesCue + " cycles");
			itemPresenter.addItem(sequence[0], cyclesCue * ASSUMED_CYCLE_TIME);

			// measure activations
			itemPresenter.addCommand("PRINT MESSAGE Recalled activation after 0 cycles:");
			itemPresenter.addCommand("PRINT UNITS");
			for (int cyclesAfter = 1; cyclesAfter <= 100; cyclesAfter++) {
				itemPresenter.addBreak(1 * ASSUMED_CYCLE_TIME);
				itemPresenter.addCommand("PRINT MESSAGE Recalled activation after " + cyclesAfter + " cycles:");
				itemPresenter.addCommand("PRINT UNITS");
			}

			// clear network
			itemPresenter.addCommand("PRINT MESSAGE Clearing network");
			itemPresenter.addCommand("CLEARNETWORK");

		}

		itemPresenter.run();
	}

}
