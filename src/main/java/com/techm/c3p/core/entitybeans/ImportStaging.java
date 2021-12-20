package com.techm.c3p.core.entitybeans;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="c3p_t_ds_import_staging")
public class ImportStaging{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_row_id", length = 11)
	private int rowId;
	
	@Column(name = "is_seq_id", length = 16)
	private int seqId;
	
	@Column(name = "is_row_status", length = 20)
	private String rowStatus;
	
	@Column(name = "is_row_error_code", length = 25)
	private String rowErrorCode;
	
	@Column(name = "is_import_id", length = 30)
	private String importId;
	
	@Column(name = "is_seq_1", length = 255)
	private String seq_1;
	
	@Column(name = "is_seq_2", length = 255)
	private String seq_2;
	
	@Column(name = "is_seq_3", length = 255)
	private String seq_3;
	
	@Column(name = "is_seq_4", length = 255)
	private String seq_4;
	
	@Column(name = "is_seq_5", length = 255)
	private String seq_5;
	
	@Column(name = "is_seq_6", length = 255)
	private String seq_6;
	
	@Column(name = "is_seq_7", length = 255)
	private String seq_7;
	
	@Column(name = "is_seq_8", length = 255)
	private String seq_8;
	
	@Column(name = "is_seq_9", length = 255)
	private String seq_9;
	
	@Column(name = "is_seq_10", length = 255)
	private String seq_10;
	
	@Column(name = "is_seq_11", length = 255)
	private String seq_11;
	
	@Column(name = "is_seq_12", length = 255)
	private String seq_12;
	
	@Column(name = "is_seq_13", length = 255)
	private String seq_13;
	
	@Column(name = "is_seq_14", length = 255)
	private String seq_14;
	
	@Column(name = "is_seq_15", length = 255)
	private String seq_15;
	
	@Column(name = "is_seq_16", length = 255)
	private String seq_16;
	
	@Column(name = "is_seq_17", length = 255)
	private String seq_17;
	
	@Column(name = "is_seq_18", length = 255)
	private String seq_18;
	
	@Column(name = "is_seq_19", length = 255)
	private String seq_19;
	
	@Column(name = "is_seq_20", length = 255)
	private String seq_20;
	
	@Column(name = "is_seq_21", length = 255)
	private String seq_21;
	
	@Column(name = "is_seq_22", length = 255)
	private String seq_22;
	
	@Column(name = "is_seq_23", length = 255)
	private String seq_23;
	
	@Column(name = "is_seq_24", length = 255)
	private String seq_24;

	@Column(name = "is_seq_25", length = 255)
	private String seq_25;
	
	@Column(name = "is_seq_26", length = 255)
	private String seq_26;
	
	@Column(name = "is_seq_27", length = 255)
	private String seq_27;
	
	@Column(name = "is_seq_28", length = 255)
	private String seq_28;
	
	@Column(name = "is_seq_29", length = 255)
	private String seq_29;
	
	@Column(name = "is_seq_30", length = 255)
	private String seq_30;
	
	@Column(name = "is_seq_31", length = 255)
	private String seq_31;
	
	@Column(name = "is_seq_32", length = 255)
	private String seq_32;
	
	@Column(name = "is_seq_33", length = 255)
	private String seq_33;
	
	@Column(name = "is_seq_34", length = 255)
	private String seq_34;
	
	@Column(name = "is_seq_35", length = 255)
	private String seq_35;
	
	@Column(name = "is_seq_36", length = 255)
	private String seq_36;
	
	@Column(name = "is_seq_37", length = 255)
	private String seq_37;
	
	@Column(name = "is_seq_38", length = 255)
	private String seq_38;
	
	@Column(name = "is_seq_39", length = 255)
	private String seq_39;
	
	@Column(name = "is_seq_40", length = 255)
	private String seq_40;
	
	@Column(name = "is_seq_41", length = 255)
	private String seq_41;
	
	@Column(name = "is_seq_42", length = 255)
	private String seq_42;
	
	@Column(name = "is_seq_43", length = 255)
	private String seq_43;
	
	@Column(name = "is_seq_44", length = 255)
	private String seq_44;
	
	@Column(name = "is_seq_45", length = 255)
	private String seq_45;
	
	@Column(name = "is_seq_46", length = 255)
	private String seq_46;
	
	@Column(name = "is_seq_47", length = 255)
	private String seq_47;
	
	@Column(name = "is_seq_48", length = 255)
	private String seq_48;
	
	@Column(name = "is_seq_49", length = 255)
	private String seq_49;
	
	@Column(name = "is_seq_50", length = 255)
	private String seq_50;

	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public int getSeqId() {
		return seqId;
	}

	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}

	public String getRowStatus() {
		return rowStatus;
	}

	public void setRowStatus(String rowStatus) {
		this.rowStatus = rowStatus;
	}

	public String getRowErrorCode() {
		return rowErrorCode;
	}

	public void setRowErrorCode(String rowErrorCode) {
		this.rowErrorCode = rowErrorCode;
	}

	public String getImportId() {
		return importId;
	}

	public void setImportId(String importId) {
		this.importId = importId;
	}

	public String getSeq_1() {
		return seq_1;
	}

	public void setSeq_1(String seq_1) {
		this.seq_1 = seq_1;
	}

	public String getSeq_2() {
		return seq_2;
	}

	public void setSeq_2(String seq_2) {
		this.seq_2 = seq_2;
	}

	public String getSeq_3() {
		return seq_3;
	}

	public void setSeq_3(String seq_3) {
		this.seq_3 = seq_3;
	}

	public String getSeq_4() {
		return seq_4;
	}

	public void setSeq_4(String seq_4) {
		this.seq_4 = seq_4;
	}

	public String getSeq_5() {
		return seq_5;
	}

	public void setSeq_5(String seq_5) {
		this.seq_5 = seq_5;
	}

	public String getSeq_6() {
		return seq_6;
	}

	public void setSeq_6(String seq_6) {
		this.seq_6 = seq_6;
	}

	public String getSeq_7() {
		return seq_7;
	}

	public void setSeq_7(String seq_7) {
		this.seq_7 = seq_7;
	}

	public String getSeq_8() {
		return seq_8;
	}

	public void setSeq_8(String seq_8) {
		this.seq_8 = seq_8;
	}

	public String getSeq_9() {
		return seq_9;
	}

	public void setSeq_9(String seq_9) {
		this.seq_9 = seq_9;
	}

	public String getSeq_10() {
		return seq_10;
	}

	public void setSeq_10(String seq_10) {
		this.seq_10 = seq_10;
	}

	public String getSeq_11() {
		return seq_11;
	}

	public void setSeq_11(String seq_11) {
		this.seq_11 = seq_11;
	}

	public String getSeq_12() {
		return seq_12;
	}

	public void setSeq_12(String seq_12) {
		this.seq_12 = seq_12;
	}

	public String getSeq_13() {
		return seq_13;
	}

	public void setSeq_13(String seq_13) {
		this.seq_13 = seq_13;
	}

	public String getSeq_14() {
		return seq_14;
	}

	public void setSeq_14(String seq_14) {
		this.seq_14 = seq_14;
	}

	public String getSeq_15() {
		return seq_15;
	}

	public void setSeq_15(String seq_15) {
		this.seq_15 = seq_15;
	}

	public String getSeq_16() {
		return seq_16;
	}

	public void setSeq_16(String seq_16) {
		this.seq_16 = seq_16;
	}

	public String getSeq_17() {
		return seq_17;
	}

	public void setSeq_17(String seq_17) {
		this.seq_17 = seq_17;
	}

	public String getSeq_18() {
		return seq_18;
	}

	public void setSeq_18(String seq_18) {
		this.seq_18 = seq_18;
	}

	public String getSeq_19() {
		return seq_19;
	}

	public void setSeq_19(String seq_19) {
		this.seq_19 = seq_19;
	}

	public String getSeq_20() {
		return seq_20;
	}

	public void setSeq_20(String seq_20) {
		this.seq_20 = seq_20;
	}

	public String getSeq_21() {
		return seq_21;
	}

	public void setSeq_21(String seq_21) {
		this.seq_21 = seq_21;
	}

	public String getSeq_22() {
		return seq_22;
	}

	public void setSeq_22(String seq_22) {
		this.seq_22 = seq_22;
	}

	public String getSeq_23() {
		return seq_23;
	}

	public void setSeq_23(String seq_23) {
		this.seq_23 = seq_23;
	}

	public String getSeq_24() {
		return seq_24;
	}

	public void setSeq_24(String seq_24) {
		this.seq_24 = seq_24;
	}

	public String getSeq_25() {
		return seq_25;
	}

	public void setSeq_25(String seq_25) {
		this.seq_25 = seq_25;
	}

	public String getSeq_26() {
		return seq_26;
	}

	public void setSeq_26(String seq_26) {
		this.seq_26 = seq_26;
	}

	public String getSeq_27() {
		return seq_27;
	}

	public void setSeq_27(String seq_27) {
		this.seq_27 = seq_27;
	}

	public String getSeq_28() {
		return seq_28;
	}

	public void setSeq_28(String seq_28) {
		this.seq_28 = seq_28;
	}

	public String getSeq_29() {
		return seq_29;
	}

	public void setSeq_29(String seq_29) {
		this.seq_29 = seq_29;
	}

	public String getSeq_30() {
		return seq_30;
	}

	public void setSeq_30(String seq_30) {
		this.seq_30 = seq_30;
	}

	public String getSeq_31() {
		return seq_31;
	}

	public void setSeq_31(String seq_31) {
		this.seq_31 = seq_31;
	}

	public String getSeq_32() {
		return seq_32;
	}

	public void setSeq_32(String seq_32) {
		this.seq_32 = seq_32;
	}

	public String getSeq_33() {
		return seq_33;
	}

	public void setSeq_33(String seq_33) {
		this.seq_33 = seq_33;
	}

	public String getSeq_34() {
		return seq_34;
	}

	public void setSeq_34(String seq_34) {
		this.seq_34 = seq_34;
	}

	public String getSeq_35() {
		return seq_35;
	}

	public void setSeq_35(String seq_35) {
		this.seq_35 = seq_35;
	}

	public String getSeq_36() {
		return seq_36;
	}

	public void setSeq_36(String seq_36) {
		this.seq_36 = seq_36;
	}

	public String getSeq_37() {
		return seq_37;
	}

	public void setSeq_37(String seq_37) {
		this.seq_37 = seq_37;
	}

	public String getSeq_38() {
		return seq_38;
	}

	public void setSeq_38(String seq_38) {
		this.seq_38 = seq_38;
	}

	public String getSeq_39() {
		return seq_39;
	}

	public void setSeq_39(String seq_39) {
		this.seq_39 = seq_39;
	}

	public String getSeq_40() {
		return seq_40;
	}

	public void setSeq_40(String seq_40) {
		this.seq_40 = seq_40;
	}

	public String getSeq_41() {
		return seq_41;
	}

	public void setSeq_41(String seq_41) {
		this.seq_41 = seq_41;
	}

	public String getSeq_42() {
		return seq_42;
	}

	public void setSeq_42(String seq_42) {
		this.seq_42 = seq_42;
	}

	public String getSeq_43() {
		return seq_43;
	}

	public void setSeq_43(String seq_43) {
		this.seq_43 = seq_43;
	}

	public String getSeq_44() {
		return seq_44;
	}

	public void setSeq_44(String seq_44) {
		this.seq_44 = seq_44;
	}

	public String getSeq_45() {
		return seq_45;
	}

	public void setSeq_45(String seq_45) {
		this.seq_45 = seq_45;
	}

	public String getSeq_46() {
		return seq_46;
	}

	public void setSeq_46(String seq_46) {
		this.seq_46 = seq_46;
	}

	public String getSeq_47() {
		return seq_47;
	}

	public void setSeq_47(String seq_47) {
		this.seq_47 = seq_47;
	}

	public String getSeq_48() {
		return seq_48;
	}

	public void setSeq_48(String seq_48) {
		this.seq_48 = seq_48;
	}

	public String getSeq_49() {
		return seq_49;
	}

	public void setSeq_49(String seq_49) {
		this.seq_49 = seq_49;
	}

	public String getSeq_50() {
		return seq_50;
	}

	public void setSeq_50(String seq_50) {
		this.seq_50 = seq_50;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rowId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImportStaging other = (ImportStaging) obj;
		if (rowId != other.rowId)
			return false;
		return true;
	}
	

}