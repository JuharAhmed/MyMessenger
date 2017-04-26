package tr.edu.iyte.AsmSimulator;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.ApplicationInfo;
        import android.content.pm.PackageManager;
        import android.media.MediaRecorder;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Messenger;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.widget.Button;
        import android.widget.EditText;

        import java.util.List;

/**
 * Created by iyte on 28.05.2015.
 */
public class AsmSimulator extends ActionBarActivity{


    private static final String TAG5 = "Test Message";

    public static String callingAppPackageName;
    public static int callingAppUID;
    //String serviceName;
    public static Context callerContext;
    static List<ApplicationInfo> packages;
    public static Messenger pdmMessenger = null;
    public static boolean isBoundToPDM=false;
    public static Intent intent;
    public static ComponentName componentName;
    static final String APP_ID = "APP_ID";
    static final String PERMISSION="PERMISSION";
    static final String ALLOW="ALLOW";
    static final String DENY="DENY";
    static final String RESPONSE="RESPONSE";


    private static AsmSimulator instance = null;
    static final String PROVIDER_NAME = "tr.edu.iyte.ca_arbac.PDM";
    static final String URL = "content://" + PROVIDER_NAME;
    static final Uri CONTENT_URI = Uri.parse(URL);


    public static AsmSimulator getInstance(Context appContext) {
        if (instance == null)
            instance = new AsmSimulator(appContext);
        return instance;
    }

    private AsmSimulator(Context appContext) {
        Log.i(TAG5, "AsmSimulator Constructed");
        callerContext = appContext;
    }


    private String getDecisionFromCaarbacSystem(int callingAppUID, String serviceName) {

        String response=DENY;
        String permission=null;
        if(serviceName.equals("LOCATION"))
            permission= "ACCESS_FINE_LOCATION";
        else if(serviceName.equals("MEDIA_RECORDER"))
            permission= "RECORD_AUDIO";

        Log.i(TAG5,permission);

        // String URL = "content://" + PROVIDER_NAME + "/" + TABLE_PATH + "/" + callingAppUID ;
        // Uri QUERY_URI = Uri.parse(URL);

        Bundle permissionRequest=new Bundle();
        permissionRequest.putInt(APP_ID,callingAppUID);
        permissionRequest.putString(PERMISSION,permission);
        Bundle b=callerContext.getContentResolver().call(CONTENT_URI,"myMethod",null,permissionRequest);
        response=b.getString(RESPONSE);
       // Log.i(TAG5, "Response:"+response);

        return response;
    }


    public Object getCaarbacSystemService(String serviceName) {

        String appPermission = DENY;
        Object service = null;

        if(callerContext==null)
            Log.i(TAG5, "callerContext is null inside getCaarbacSystemService");
        callingAppPackageName = callerContext.getPackageName();
        Log.i(TAG5, callingAppPackageName);
        callingAppUID = getApplicationId(callingAppPackageName);

        Log.i(TAG5, "Calling App ID:"+String.valueOf(callingAppUID));
        long startTime = System.currentTimeMillis();
        appPermission = getDecisionFromCaarbacSystem(callingAppUID, serviceName);
        long stopTime = System.currentTimeMillis();
        long callBackTime = stopTime - startTime;
        Log.i(TAG5,"Call Back Time Delay"+String.valueOf(callBackTime));

        if(!appPermission.equals(ALLOW)){
            throw new SecurityException("The application: \""+callingAppPackageName+"\" \n is trying to access \""+serviceName+"\" without being granted permission");
        }
        long startTime2 = System.currentTimeMillis();
            service = getService(serviceName);
        long stopTime2 = System.currentTimeMillis();
        long normalTime = stopTime2 - startTime2;
        Log.i(TAG5,"Normal Time Delay"+String.valueOf(normalTime));
        Log.i(TAG5,"Total Time Delay"+String.valueOf(normalTime+callBackTime));
           return service;



    }

    private Object getService(String serviceName) {
        Object service = null;
        switch (serviceName) {
            case "LOCATION":
                service = callerContext.getSystemService(Context.LOCATION_SERVICE);
                break;
            case "MEDIA_RECORDER":
                service = new MediaRecorder();
                break;

            default:
                service = null;
        }
        return service;
    }


    public int getApplicationId(String callingAppPackageName) {
        int UID=0;

        final PackageManager pm = callerContext.getPackageManager();
        if(pm!=null)
            packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for(ApplicationInfo appInfo:packages){
            if (appInfo.packageName.equals(callingAppPackageName)){
                UID = appInfo.uid;
                break;
            }
        }

        return UID;
    }
}
