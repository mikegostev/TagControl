package uk.ac.ebi.age.tagcontrol;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import uk.ac.ebi.age.admin.shared.Constants;
import uk.ac.ebi.biosd.shared.TagControlConstants;

public class TagControl
{
 static final String    usage   = "java -jar TagControl.jar -h URL -u USER -p PASS [-smp ID1,ID2,...] [-grp ID1,ID2,...] [-sbm ID1,ID2,...] -a T1,T2,... -r T3,T4,...  -l";

 private static Options options = new Options();

 public static void main(String[] args)
 {
  CmdLineParser parser = new CmdLineParser(options);

  if( args.length == 0 )
  {
   System.err.println(usage);
   parser.printUsage(System.err);
   System.exit(10);
   return;
  }

  try
  {
   parser.parseArgument(args);
  }
  catch(CmdLineException e)
  {
   System.err.println(e.getMessage());
   System.err.println(usage);
   parser.printUsage(System.err);
   System.exit(10);
   return;
  }

  if(options.getDatabaseURL() == null)
  {
   System.err.println("Database URI is required for remote operations");
   System.exit(1);
   return;
  }
  else if(!options.getDatabaseURL().endsWith("/"))
   options.setDatabaseURI(options.getDatabaseURL() + "/");

  if(options.getUser() == null)
  {
   System.err.println("User name is required for remote operations");
   System.exit(1);
   return;
  }
  
  String sbmList = null;
  
  if( options.getSubmissionIdList() != null )
  {
   if( options.getSubmissionIDs() != null )
   {
    System.err.println("Options -i and -smb can't be user simultaneously");
    System.exit(1);
    return;
   }
   
   sbmList = options.getSubmissionIdList();
  }
  else
   sbmList = options.getSubmissionIDs();
  
  if( options.getTagStringAdd() == null && options.getTagStringDel() == null && ! options.isListTags())
  {
   System.err.println("Tags for insertion/deletion or -l option should be specified");
   System.exit(1);
   return;
  }

  if( sbmList == null && options.getGroupIDs() == null && options.getSampleIDs() == null )
  {
   System.err.println("Sample, group or submission identifiers should be specified for the operation");
   System.exit(1);
   return;
  }
  
  boolean ok = false;
  String sessionKey = null;
  DefaultHttpClient httpclient = null;

  try
  {

   httpclient = new DefaultHttpClient();
   HttpPost httpost = new HttpPost(options.getDatabaseURL() + "Login");

   List<NameValuePair> nvps = new ArrayList<NameValuePair>();
   nvps.add(new BasicNameValuePair("username", options.getUser()));
   nvps.add(new BasicNameValuePair("password", options.getPassword() != null ? options.getPassword() : ""));

   httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

   HttpResponse response = httpclient.execute(httpost);

   if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
   {
    System.err.println("Login failed. Server response code is: " + response.getStatusLine().getStatusCode());
    System.exit(2);
    return;
   }

   HttpEntity ent = response.getEntity();

   String respStr = EntityUtils.toString(ent).trim();

   if(respStr.startsWith("OK:"))
   {
    sessionKey = respStr.substring(3);
   }
   else
   {
    System.err.println("Login failed: " + respStr);
    return;
   }

   EntityUtils.consume(ent);

   ok = true;
  }
  catch(Throwable e)
  {
   System.err.println("ERROR: Login failed: " + e.getMessage()+" ("+e.getClass().getName()+")");
  }
  finally
  {
   if(!ok)
   {
    httpclient.getConnectionManager().shutdown();
    System.exit(3);
    return;
   }
  }
  
  ok=false;
  
  try
  {
   HttpPost httpost = new HttpPost(options.getDatabaseURL() + "upload");

   List<NameValuePair> nvps = new ArrayList<NameValuePair>();
   nvps.add(new BasicNameValuePair(Constants.sessionKey, sessionKey));
   nvps.add(new BasicNameValuePair(Constants.serviceHandlerParameter, uk.ac.ebi.biosd.shared.Constants.BIOSD_TAG_CONTROL_COMMAND));
   
   if( sbmList != null )
    nvps.add(new BasicNameValuePair(TagControlConstants.SUBMISSON_ID, sbmList));

   if( options.getSampleIDs() != null )
    nvps.add(new BasicNameValuePair(TagControlConstants.SAMPLE_ID, options.getSampleIDs()));

   if( options.getGroupIDs() != null )
    nvps.add(new BasicNameValuePair(TagControlConstants.GROUP_ID, options.getGroupIDs()));

   if( options.getTagStringAdd() != null )
    nvps.add(new BasicNameValuePair(TagControlConstants.TAGS_TO_ADD, options.getTagStringAdd()));

   if( options.getTagStringDel() != null )
    nvps.add(new BasicNameValuePair(TagControlConstants.TAGS_TO_REMOVE, options.getTagStringDel()));

   if( options.isListTags() )
    nvps.add(new BasicNameValuePair(TagControlConstants.LIST_TAGS, "on"));
   
   httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

   HttpResponse response = httpclient.execute(httpost);
   
   HttpEntity ent = response.getEntity();

   String respStr = EntityUtils.toString(ent).trim();
   
   System.out.println( respStr );

   
   if( respStr.startsWith("OK") )
    ok = true;
  }
  catch(Exception e)
  {
   System.out.println("Communication error: " + e.getMessage() + " (" + e.getClass().getName() + ")");
  }
  finally
  {
   httpclient.getConnectionManager().shutdown();
  }
  
  System.exit(ok?0:1);

 }

}
