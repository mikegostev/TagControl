package uk.ac.ebi.age.tagcontrol;

import org.kohsuke.args4j.Option;


public class Options {

    @Option(name = "-h", usage = "Database base URL", metaVar = "URL")
    private String databaseURL;

    @Option(name = "-u", usage = "User name", metaVar = "USER")
    private String user;

    @Option(name = "-p", usage = "User password", metaVar = "PASS")
    private String password;

    @Option(name = "-i", usage = "Submissions ID list for add/remove operation. Same as -smb. (obsolete) ", metaVar = "IDS")
    private String submissionIdList;

    @Option(name = "-a", usage = "Tags list to add. Example: Security:Public,Manager:Enable=2012-01-01")
    private String tagStringAdd;

    @Option(name = "-r", usage = "Tags list to remove. Example: Security:Public,Manager:Enable")
    private String tagStringDel;

    @Option(name = "-l", usage = "List tags")
    private boolean listTags;

    @Option(name = "-sbm", usage = "Submissions ID list")
    private String submissionIDs;

    @Option(name = "-smp", usage = "Sample ID list")
    private String sampleIDs;
    
    @Option(name = "-grp", usage = "Group ID list")
    private String groupIDs;

 public String getDatabaseURL()
 {
  return databaseURL;
 }

 public String getUser()
 {
  return user;
 }

 public String getPassword()
 {
  return password;
 }

 public void setDatabaseURI(String databaseURI)
 {
  this.databaseURL = databaseURI;
 }

 public String getSubmissionIdList()
 {
  return submissionIdList;
 }

 public void setSubmissionIdList(String submissionIdList)
 {
  this.submissionIdList = submissionIdList;
 }

 public String getTagStringAdd()
 {
  return tagStringAdd;
 }

 public String getTagStringDel()
 {
  return tagStringDel;
 }

 public boolean isListTags()
 {
  return listTags;
 }

 public String getSubmissionIDs()
 {
  return submissionIDs;
 }

 public String getSampleIDs()
 {
  return sampleIDs;
 }

 public String getGroupIDs()
 {
  return groupIDs;
 }

}


