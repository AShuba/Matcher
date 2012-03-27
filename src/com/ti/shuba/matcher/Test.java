package com.ti.shuba.matcher;

/**
 * Test object from the TITAN test file description
 * 
 * TODO to add all fields from the TITAN file description
 * 
 * @author Anatoliy Shuba, created  Mar 26, 2012
 */
public class Test {
    private String author;
    private String testID;
    private String description;
    private String dateofcreation;

    /**
     * Get the value of dateofcreation
     *
     * @return the value of dateofcreation
     */
    public String getDateofcreation() {
        return dateofcreation;
    }

    /**
     * Set the value of dateofcreation
     *
     * @param dateofcreation new value of dateofcreation
     */
    public void setDateofcreation(String dateofcreation) {
        this.dateofcreation = dateofcreation;
    }

    /**
     * Get the value of description
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description
     *
     * @param description new value of description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the value of testID
     *
     * @return the value of testID
     */
    public String getTestID() {
        return testID;
    }

    /**
     * Set the value of testID
     *
     * @param testID new value of testID
     */
    public void setTestID(String testID) {
        this.testID = testID;
    }

    /**
     * Get the value of author
     *
     * @return the value of author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the value of author
     *
     * @param author new value of author
     */
    public void setAuthor(String author) {
        this.author = author;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Test details - ").append(getTestID()).append("\n");
        sb.append("Author - ").append(getAuthor()).append("\n");
        sb.append("Date of creation - ").append(getDateofcreation()).append("\n");
        sb.append("Description - ").append(getDescription()).append("\n");
        return sb.toString();
    }

}
