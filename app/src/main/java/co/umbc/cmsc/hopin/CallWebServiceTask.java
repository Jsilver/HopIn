package co.umbc.cmsc.hopin;

import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by crypton on 4/30/17.
 */

public class CallWebServiceTask extends AsyncTask<String, Integer, String> {

    URL url;
    String response = "";

    /**
     * Override this method to perform a computation on a background thread. The specified parameters are the parameters passed to {@link #execute} by the caller of this task.
     *
     * This method can call {@link #publishProgress} to publish updates on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected String doInBackground(String... params) {

        switch (params[0]) {
            case "getdrivers":
                String requestURL = "http://10.200.54.39/hopinservice/api/v0/setdriver.php";

                break;
            case "getriders":
                break;
        }

        return null;
    }

} // end class