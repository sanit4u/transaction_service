package de.sanit4u.transaction.dto;

public class SumDTO {

	private double sum;

	public SumDTO(double sum) {
		this.sum = sum;
	}

	public double getSum() {
		return sum;
	}

	@Override
	public String toString() {
		return "Sum [sum=" + sum + "]";
	}

}
