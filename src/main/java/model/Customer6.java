package model;


public class Customer6 extends User6 {
    private String address;
    private MembershipCard6 membershipCard;

    public String getAddress() { return address; }
    public void setAddress(String value) { address = value; }
    public MembershipCard6 getMembershipCard() { return membershipCard; }
    public void setMembershipCard(MembershipCard6 value) { membershipCard = value; }
}
