package com.test.ai;

import com.microsoft.cognitiveservices.speech.CancellationDetails;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.intent.IntentRecognitionResult;
import com.microsoft.cognitiveservices.speech.intent.IntentRecognizer;

import java.util.Dictionary;
import java.util.concurrent.ExecutionException;

public class App {

    public static void main(String args[]) throws ExecutionException, InterruptedException {
        intentPatternMatchingWithMicrophone();
    }


    public static void intentPatternMatchingWithMicrophone() throws InterruptedException, ExecutionException {

        final String key = "73c46016116c431abbb5d42827bc2133";

        SpeechConfig config = SpeechConfig.fromSubscription(key, "eastus");

        try (IntentRecognizer intentRecognizer = new IntentRecognizer(config)) {
            intentRecognizer.addIntent("Restart {cname} in {ename}.", "RestartComponent");
            intentRecognizer.addIntent("Go to floor {floorName}.", "ChangeFloors");
            intentRecognizer.addIntent("{action} the door.", "OpenCloseDoor");

            System.out.println("Say something...");

            IntentRecognitionResult result = intentRecognizer.recognizeOnceAsync().get();

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                System.out.println("RECOGNIZED: Text= " + result.getText());
                System.out.println(String.format("%17s", "Intent not recognized."));
            }
            else if (result.getReason() == ResultReason.RecognizedIntent) {
                System.out.println("RECOGNIZED: Text= " + result.getText());
                System.out.println(String.format("%17s %s", "Intent Id=", result.getIntentId() + "."));
                Dictionary<String, String> entities = result.getEntities();

                if (entities.get("cname") != null) {
                    System.out.println(String.format("%17s %s", "cname=", entities.get("cname")));
                }

                if (entities.get("ename") != null) {
                    System.out.println(String.format("%17s %s", "ename=", entities.get("ename")));
                }

                if (entities.get("floorName") != null) {
                    System.out.println(String.format("%17s %s", "FloorName=", entities.get("floorName")));
                }
                if (entities.get("action") != null) {
                    System.out.println(String.format("%17s %s", "Action=", entities.get("action")));
                }
            }
            else if (result.getReason() == ResultReason.NoMatch) {
                System.out.println("NOMATCH: Speech could not be recognized.");
            }
            else if (result.getReason() == ResultReason.Canceled) {
                CancellationDetails cancellation = CancellationDetails.fromResult(result);
                System.out.println("CANCELED: Reason=" + cancellation.getReason());

                if (cancellation.getReason() == CancellationReason.Error)
                {
                    System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                    System.out.println("CANCELED: Did you update the subscription info?");
                }
            }
        }
    }
}
