


public class User {
	private String firstName,familyName;
	private String username;
	private int penaltyPoints=0;
	
	public User(String firstName,String familyName,String username){
		this.firstName=firstName;
		this.familyName=familyName;
		this.username= username;
	}
	public void penalize(){
		penaltyPoints++;
	}
	public void setPenaltyPoints(int penaltyPoints){
		this.penaltyPoints = penaltyPoints;
	}
	public int getPenaltyPoints(){
		return penaltyPoints;
	}
	public String getFirstName() {
		return firstName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getUsername() {
		return username;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
}
