package haven;

public class Category {

	public static String getCat1(int num) {
		int opcode = num >>> 26;
		int offset, rs, rt, base, temp;
		StringBuilder ret = new StringBuilder();
		switch (opcode) {
		case 0:
			ret.append("J");
			int instr_index = num << 2 & 0x03ffffff;
			ret.append(" #" + instr_index);
			break;
		case 2:
			// Format: BEQ rs, rt, offset
			ret.append("BEQ");
			offset = num << 16 >> 14;
			temp = num >>> 16;
			rt = temp & 0x1f;
			rs = temp >>> 5 & 0x1f;
			ret.append(" R" + rs + ", R" + rt + ", #" + offset);
			break;
		case 4:
			// Format: BGTZ rs, offset
			ret.append("BGTZ");
			offset = num << 16 >> 14;
			rs = num >>> 21 & 0x1f;
			ret.append(" R" + rs + ", #" + offset);
			break;
		case 5:
			ret.append("BREAK");
			break;
		case 6:
			// SW rt, offset(base)
			ret.append("SW");
			offset = num << 16 >> 16;
			temp = num >>> 16;
			rt = temp & 0x1f;
			base = temp >> 5 & 0x1f;
			ret.append(" R" + rt + ", " + offset + "(R" + base + ")");
			break;
		case 7:
			// Format: LW rt, offset(base)
			ret.append("LW");
			offset = num << 16 >> 16;
			temp = num >>> 16;
			rt = temp & 0x1f;
			base = temp >> 5 & 0x1f;
			ret.append(" R" + rt + ", " + offset + "(R" + base + ")");
			break;
		default:
			ret.append(num);
			return ret.toString();
		}
		return ret.toString();
	}

	public static String getCat2(int num) {
		// 110 rs (5 bits) rt (5 bits) opcode (3 bits) rd (5 bits) 00000000000
		// ADD rd, rs, rt
		int temp = num >>> 11;
		int rd = temp & 0x1f;
		temp >>>= 5;
		int opcode = temp & 0x7;
		temp >>>= 3;
		int rt = temp & 0x1f;
		temp >>>= 5;
		int rs = temp & 0x1f;

		StringBuilder ret = new StringBuilder();
		switch (opcode) {
		case 0:
			ret.append("ADD");
			break;
		case 1:
			ret.append("SUB");
			break;
		case 2:
			ret.append("MUL");
			break;
		case 3:
			ret.append("AND");
			break;
		case 4:
			ret.append("OR");
			break;
		case 5:
			ret.append("XOR");
			break;
		case 6:
			ret.append("NOR");
			break;
		default:
			ret.append(num);
			return ret.toString();
		}
		ret.append(" R" + rd + ", R" + rs + ", R" + rt);
		return ret.toString();
	}

	public static String getCat3(int num) {
		// 111 rs (5 bits) rt (5 bits) opcode (3 bits) immediate_value (16 bits)
		// ADDI rt, rs, immediate
		int immediate_value = num & 0xffff;
		int temp = num >>> 16;
		int opcode = temp & 0x7;
		temp >>>= 3;
		int rt = temp & 0x1f;
		temp >>>= 5;
		int rs = temp & 0x1f;

		StringBuilder ret = new StringBuilder();
		switch (opcode) {
		case 0:
			ret.append("ADDI");
			break;
		case 1:
			ret.append("ANDI");
			break;
		case 2:
			ret.append("ORI");
			break;
		case 3:
			ret.append("XORI");
			break;

		default:
			ret.append(num);
			return ret.toString();
		}
		ret.append(" R" + rt + ", R" + rs + ", #" + immediate_value + "");
		return ret.toString();
	}
}
