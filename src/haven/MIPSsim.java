package haven;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MIPSsim {

	private static String disassemblerLine(String line, int lineNumber) {
		StringBuilder sb = new StringBuilder(line);
		int instruction = (int) Long.parseLong(line, 2);
		sb.append("\t" + lineNumber + '\t');

		int temp = instruction >>> 29;
		switch (temp) {
		case 0:
			sb.append(Category.getCat1(instruction));
			break;
		case 6:
			sb.append(Category.getCat2(instruction));
			break;
		case 7:
			sb.append(Category.getCat3(instruction));
			break;
		default:
			sb.append(instruction);
			break;
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("error:参数个数不唯一");
			return;
		}
		File filein = new File(args[0]);
		File fileOut = new File("generated_disassembly.txt");
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String line = null;
		int address = 128;

		// for simulation
		ArrayList<String> instructions = new ArrayList<String>();
		ArrayList<Integer> data = new ArrayList<Integer>();
		int dataAddress;

		try {
			if (!fileOut.exists()) {
				fileOut.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(fileOut, false));
			reader = new BufferedReader(new FileReader(filein));

			// code segment
			while ((line = reader.readLine()) != null) {
				line = disassemblerLine(line, address);

				// for simulation
				instructions.add(line.substring(line.lastIndexOf('\t') + 1));
				writer.write(line);
				writer.newLine();
				address += 4;
				if (line.endsWith("BREAK")) {
					break;
				}
			}

			dataAddress = address;

			// data segment
			while ((line = reader.readLine()) != null) {
				int number = (int) Long.parseLong(line, 2);

				// for simulation
				data.add(number);

				line = line + "\t" + address + '\t' + number;

				writer.write(line);
				reader.mark(1);
				if (-1 != reader.read()) {
					writer.newLine();
				}
				reader.reset();
				address += 4;
			}

			writer.flush();

			// simulation
			Simulation simu = new Simulation(data, new Context(dataAddress),
					instructions);
			simu.simulate();

		} catch (FileNotFoundException e) {
			System.out.println("文件不存在");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e1) {
				}
			}
		}
	}
}
