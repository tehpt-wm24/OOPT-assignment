public class Guest extends Person{
	private String nationality;
	private String specialRequests;
	private int discount;

	public Guest() {
		this(0, "", 0, "", "", 0);
	}

	public Guest(int guestId, String name, long contact, String nationality, String specialRequests, int discount) {
		super(guestId, name, contact);
		this.nationality = nationality;
		this.specialRequests = specialRequests;
		this.discount = discount;
	}
	
	// Getter
	public int getGuestId() {
        return super.getId();
    }

	public String getNationality() {
		return nationality;
	}

	public String getSpecialRequests() {
		return specialRequests;
	}

	public int getDiscount() {
		return discount;
	}

	// Setter
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public void setSpecialRequests(String specialRequests) {
		this.specialRequests = specialRequests;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public void setGuestId(int guestId) {
        super.setId(guestId); 
    }

	public String toString() {
		return super.toString() + "\n" + "Nationality: " + nationality + "\n" + "Special Requests: " + specialRequests + "\n" + "Discount: " + discount + "%";
	}

	public boolean equals(Object obj) {
		if(obj instanceof Guest) {
			return super.equals(obj);
		} else {
			return false;
		}
	}
}