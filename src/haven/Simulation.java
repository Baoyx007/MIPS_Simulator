package haven;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Simulation {

	private int[] register;
	private int[] data;
	private Context context;
	private ArrayList<String> instructions;

	public Simulation(ArrayList<Integer> data, Context context,
			ArrayList<String> instructions) {
		this.register = new int[32];
		this.context = context;
		this.instructions = instructions;

		this.data = new int[data.size()];
		for (int i = 0; i < data.size(); i++) {
			this.data[i] = data.get(i);
		}
	}

	public void simulate() {
		File fileOut = new File("generated_simulation.txt");
		BufferedWriter writer = null;
		try {
			if (!fileOut.exists()) {
				fileOut.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(fileOut, false));

			while (true) {
				String str = instructions
						.get((context.getCurAddress() - 128) / 4);

				displayCurInstr(writer, str);
				readACycle(str);

				if (str.equals("BREAK")) {
					displayAfterInstr(writer, str, true);
					break;
				} else {
					displayAfterInstr(writer, str, false);
				}
				writer.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * --------------------<br>
	 * Cycle:1 128 ADD R1, R0, R0<br>
	 * <br>
	 * Registers<br>
	 * R00: 0 0 0 0 0 0 0 0<br>
	 * R08: 0 0 0 0 0 0 0 0<br>
	 * R16: 0 0 0 0 0 0 0 0<br>
	 * R24: 0 0 0 0 0 0 0 0<br>
	 * <br>
	 * Data<br>
	 * 184: -1 -2 -3 1 2 4 -4 10<br>
	 * 216: 7 9 1 0 -1 1 -1 0<br>
	 * <br>
	 * 
	 * @param instruction
	 *            ADD R1, R0, R0<br>
	 * @param register
	 *            r[0-31]<br>
	 * @param data
	 *            184-244<br>
	 */
	private void readACycle(String instruction) {
		String[] split = instruction.split(" ");
		boolean isjump = false;
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i < split.length; i++) {
			char c = split[i].charAt(0);
			if (c == 'R' || c == '#') {
				if (split[i].endsWith(",")) {
					list.add(Integer.parseInt(split[i].substring(1,
							split[i].length() - 1)));
				} else {
					list.add(Integer.parseInt(split[i].substring(1)));
				}
			} else {
				// 184(r6)
				int left = split[i].indexOf('(');
				list.add(Integer.parseInt(split[i].substring(0, left))
						+ register[Integer.parseInt(split[i].substring(
								left + 2, split[i].length() - 1))]);

			}
		}

		switch (split[0]) {
		case "J":
			context.setCurAddress(list.get(0));
			isjump = true;
			break;
		case "BEQ":
			// BEQ rs, rt, offset
			// BEQ R1, R2, #36
			// if rs = rt then branch
			if (register[list.get(0)] == register[list.get(1)]) {
				context.setCurAddress(context.getCurAddress() + list.get(2));
			}
			break;
		case "BGTZ":
			// BGTZ R5, #4
			if (register[list.get(0)] > 0) {
				context.setCurAddress(context.getCurAddress() + list.get(1));
			}
			break;
		case "BREAK":
			break;
		case "SW":
			// SW rt, offset(base)
			// memory[base+offset] ¡û rt
			// SW R5, 216(R6)
			data[(list.get(1) - context.getDataAddress()) / 4] = register[list
					.get(0)];
			break;
		case "LW":
			// rt ¡û memory[base+offset]
			// LW rt, offset(base)
			register[list.get(0)] = data[(list.get(1) - context
					.getDataAddress()) / 4];
			break;
		case "ADD":
			register[list.get(0)] = register[list.get(1)]
					+ register[list.get(2)];
			break;
		case "SUB":
			register[list.get(0)] = register[list.get(1)]
					- register[list.get(2)];
			break;
		case "MUL":
			register[list.get(0)] = register[list.get(1)]
					* register[list.get(2)];
			break;
		case "AND":
			register[list.get(0)] = register[list.get(1)]
					& register[list.get(2)];
			break;
		case "OR":
			register[list.get(0)] = register[list.get(1)]
					| register[list.get(2)];
			break;
		case "XOR":
			register[list.get(0)] = register[list.get(1)]
					^ register[list.get(2)];
			break;
		case "NOR":
			register[list.get(0)] = ~(register[list.get(1)] | register[list
					.get(2)]);
			break;
		case "ADDI":
			register[list.get(0)] = register[list.get(1)] + list.get(2);
			break;
		case "ANDI":
			register[list.get(0)] = register[list.get(1)] & list.get(2);
			break;
		case "ORI":
			register[list.get(0)] = register[list.get(1)] | list.get(2);
			break;
		case "XORI":
			register[list.get(0)] = ~(register[list.get(1)] | list.get(2));
			break;
		default:
			break;
		}
		if (!isjump) {
			context.setCurAddress(context.getCurAddress() + 4);
		}
		context.setCycle(context.getCycle() + 1);
	}

	private void displayCurInstr(BufferedWriter writer, String instruction)
			throws IOException {
		writer.write("--------------------");
		writer.newLine();
		writer.write("Cycle:" + context.getCycle() + "\t"
				+ context.getCurAddress() + "\t" + instruction);
		writer.newLine();
		writer.newLine();
	}

	private void displayAfterInstr(BufferedWriter writer, String instruction,
			boolean lastOne) throws IOException {

		writer.write("Registers");
		writer.newLine();

		int i = 0, j = 0;
		for (i = 0; i < 4; i++) {
			if (i > 1) {
				writer.write("R" + i * 8 + ":");
			} else {
				writer.write("R0" + i * 8 + ":");
			}
			for (j = 0; j < 8; j++) {
				writer.write("\t" + register[i * 8 + j]);
			}
			writer.newLine();
		}

		writer.newLine();
		writer.write("Data");
		writer.newLine();

		int length = data.length;
		for (i = 0; i < length / 8; i++) {
			writer.write("" + (context.getDataAddress() + i * 32) + ":");
			for (j = 0; j < 8; j++) {
				writer.write("\t" + data[i * 8 + j]);
			}
			writer.newLine();
		}
		if (!lastOne)
			writer.newLine();
	}
}
