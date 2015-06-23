package com.ozer.duyurupanosu;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.callbacks.DeviceRegistrationCallback;
import com.apigee.sdk.data.client.entities.Device;
import com.apigee.sdk.data.client.push.GCMDestination;
import com.apigee.sdk.data.client.push.GCMPayload;
import com.apigee.sdk.data.client.response.ApiResponse;
import com.google.android.gcm.GCMRegistrar;

import static com.ozer.duyurupanosu.Settings.API_URL;
import static com.ozer.duyurupanosu.Settings.APP;
import static com.ozer.duyurupanosu.Settings.NOTIFIER;
import static com.ozer.duyurupanosu.Settings.ORG;
import static com.ozer.duyurupanosu.Settings.PASSWORD;
import static com.ozer.duyurupanosu.Settings.USER;
import static com.ozer.duyurupanosu.Util.TAG;
import static com.ozer.duyurupanosu.Util.displayMessage;

public final class AppServices extends Application{

  private static ApigeeDataClient client;
  private static Device device;
  private static Context mContext;

  static ApigeeDataClient getClient(Context context) {
      mContext=context;
    if (client == null) {
    	if (ORG.equals("")) {
    		Log.e(TAG, "ORG value has not been set.");
    	} else {
    		ApigeeClient apigeeClient = new ApigeeClient(ORG,APP,API_URL,context);
    		client = apigeeClient.getDataClient();
    	}
    }
    return client;
  }

  static void loginAndRegisterForPush(final Context context) {

    if ((USER != null) && (USER.length() > 0)) {
        ApigeeDataClient dataClient = getClient(context);
    	if (dataClient != null) {
    		dataClient.authorizeAppUserAsync(USER, PASSWORD, new ApiResponseCallback() {

    			@Override
    			public void onResponse(ApiResponse apiResponse) {
    				Log.i(TAG, "login response: " + apiResponse);
    				registerPush(context);
    			}

    			@Override
    			public void onException(Exception e) {
    				displayMessage(context, "Login Exception: " + e);
    				Log.i(TAG, "login exception: " + e);
    			}
    		});
    	} else {
    		Log.e(TAG,"Data client is null, did you set ORG value in Settings.java?");
    	}
    } else {
      registerPush(context);
    }
  }
  
  static void registerPush(Context context) {

    final String regId = GCMRegistrar.getRegistrationId(context);

    if ("".equals(regId)) {
      GCMRegistrar.register(context, Settings.GCM_SENDER_ID);
    } else {
      if (GCMRegistrar.isRegisteredOnServer(context)) {
        Log.i(TAG, "Already registered with GCM");
      } else {
        AppServices.register(context, regId);
      }
    }
  }


  /**
   * Register this user/device pair on App Services.
   */
  static void register(final Context context, final String regId) {
    Log.i(TAG, "registering device: " + regId);

    ApigeeDataClient dataClient = getClient(context);
	if (dataClient != null) {

		dataClient.registerDeviceForPushAsync(dataClient.getUniqueDeviceID(), NOTIFIER, regId, null, new DeviceRegistrationCallback() {

      @Override
      public void onResponse(Device device) {
        Log.i(TAG, "register response: " + device);
        AppServices.device = device;
        //todo displayMessage(context, "Device registered as: " + regId);
        //Toast.makeText(mContext, "Kayıtlı Cihaz", Toast.LENGTH_SHORT).show();
        ApigeeDataClient dataClient = getClient(context);

        if (dataClient != null) {
        	// connect Device to current User - if there is one
        	if (dataClient.getLoggedInUser() != null) {
        		dataClient.connectEntitiesAsync("users", dataClient.getLoggedInUser().getUuid().toString(),
                                           "devices", device.getUuid().toString(),
                                           new ApiResponseCallback() {
        			@Override
        			public void onResponse(ApiResponse apiResponse) {
        				Log.i(TAG, "connect response: " + apiResponse);
        			}

        			@Override
        			public void onException(Exception e) {
        				displayMessage(context, "Connect Exception: " + e);
        				Log.i(TAG, "connect exception: " + e);
        			}
        		});
        	}
        } else {
        	Log.e(TAG,"data client is null, did you set ORG value in Settings.java?");
        }
      }

      @Override
      public void onException(Exception e) {
    	displayMessage(context, "Register Exception: " + e);
        Log.i(TAG, "register exception: " + e);
      }

      @Override
      public void onDeviceRegistration(Device device) { /* this won't ever be called */ }
    });
	} else {
		Log.e(TAG, "Data client is null, did you set ORG value in Settings.java?");
	}
  }

  static void sendMyselfANotification(final Context context,String notification) {
	  if (device == null) {
		  displayMessage(context, "Device not registered. ORG value set in Settings.java?");
	  } else {
		  ApigeeDataClient dataClient = getClient(context);
		  if (dataClient != null) {
			  GCMDestination destination = GCMDestination.destinationAllDevices();//destinationSingleDevice(device.getUuid());

			  GCMPayload payload = new GCMPayload();
			  payload.setAlertText(notification);
			  
			  dataClient.pushNotificationAsync(payload, destination, NOTIFIER, new ApiResponseCallback() {

				  @Override
				  public void onResponse(ApiResponse apiResponse) {
					  Log.i(TAG, "send response: " + apiResponse);
				  }

				  @Override
				  public void onException(Exception e) {
					  displayMessage(context, "Send Exception: " + e);
					  Log.i(TAG, "send exception: " + e);
				  }
			  });
		  } else {
			  Log.e(TAG, "data client is null, did you set ORG value in Settings.java?");
		  }
	  }
  }

  /**
   * Unregister this device within the server.
   */
  static void unregister(final Context context, final String regId) {
    Log.i(TAG, "unregistering device: " + regId);
    register(context, "");

  }
}
