[![npm version](https://badge.fury.io/js/capacitor-fullscreen-notification.svg)](https://badge.fury.io/js/capacitor-fullscreen-notification)
[![capacitor support](https://img.shields.io/badge/capacitor%20support-v5-brightgreen?logo=capacitor)](https://capacitorjs.com/)

# capacitor-fullscreen-notification

This plugin can automatically launch your app, triggered by a data push notification by the [@capacitor/push-notifications](https://github.com/ionic-team/capacitor-plugins/tree/main/push-notifications) plugin.

Use case example: Someone rings your doorbell -> your app opens and navigates to a specific route

## Supported platforms

| Platform | Supported |
| -------- | --------: |
| Android  |         ✔ |
| iOS      |         ✖ |
| Web      |         ✖ |

## Install

```bash
npm install capacitor-fullscreen-notification
npx cap sync android
```

Add this to the activity inside your AndroidManifest.xml:

```
android:showWhenLocked="true"
android:turnScreenOn="true"
```

## Android behavior

The Android OS differs between an locked and unlocked device.

### Locked

The app will only be launched instantly, when your device is in standby.

### Unlocked

If your device is not in standby, you will get a default heads up notification with the defined title, text and actionButtons. When you click on it, the app will launch.

> **HINT**  
> To identify if the app has been launched instantly or by clicking the heads up notification, you can check the property 'isNotificationActive' in the response data of your 'launch' listener. This is true on instant launch.

## Workflow

This plugin will start a fullscreen intent by a local notification, when it receives a specific data push notification.

| Firebase            |
| ------------------- |
| 1. Send a data push |

| Plugin                                                            |
| ----------------------------------------------------------------- |
| 2. Receives the data push                                         |
| 3. Sends a local notification with full screen intent (opens app) |
| 4. Emits a plugin event                                           |

| App                                         |
| ------------------------------------------- |
| 5. Listen to event and redirect to any page |

## Usage

### Trigger push

Send a data push notification (leave the field notification empty!) with the following key value pairs in the data option:

| Option                   | Description                                                                                                                                      |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`fullScreenId`**       | (Required) Identifier of the fullscreen notification                                                                                             |
| **`channelId`**          | Identifier of the notification channel                                                                                                           |
| **`channelName`**        | Name of the notification channel                                                                                                                 |
| **`channelDescription`** | Description of the notification channel                                                                                                          |
| **`title`**              | Title of the heads up notification                                                                                                               |
| **`text`**               | Text of the heads up notification                                                                                                                |
| **`timeout`**            | Timeout, for auto removing the notification                                                                                                      |
| **`vibrationPattern`**   | Stringified array of long values ([docs](<https://developer.android.com/reference/android/app/NotificationChannel#setVibrationPattern(long[])>)) |
| **`actionButtons`**      | Stringified array of ActionButton objects                                                                                                        |

#### ActionButton

| Prop       | Description              |
| ---------- | ------------------------ |
| **`id`**   | Identifier of the button |
| **`text`** | Button text              |

#### Example

```json
{
  "fullScreenId": "my-fullscreen-identifier",
  "channelId": "fullscreen-channel",
  "channelName": "My Fullscreen Channel",
  "channelDescription": "Notifications in this channel will be displayed with a fullscreen intent",
  "title": "Test notification",
  "text": "Test description",
  "timeout": "10000",
  "vibrationPattern": "[500, 300, 500, 300, 500, 300, 500, 300]",
  "actionButtons": "[{ \"id\": \"reject\", \"text\": \"Reject\" }, { \"id\": \"accept\", \"text\": \"Accept\" }]"
}
```

### App

In your app, you can listen to the launch of the application. This event is fired when the fullscreen notification opens or the heads up notification is clicked. You can also cancel the active notification, if it exists:

```javascript
import { FullScreenNotification } from 'capacitor-fullscreen-notification';

FullScreenNotification.addListener('launch', (data) => {
    ...
});

...

await FullScreenNotification.cancelNotification();
```

Inside the data of the launch event, you can have the following data:

| Prop                       | Type    | Description                                          |
| -------------------------- | ------- | ---------------------------------------------------- |
| **`fullScreenId`**         | string  | Identifier of the triggering request                 |
| **`isNotificationActive`** | boolean | Is the notification still active                     |
| **`timeout`**              | number  | (Optionally) Timeout value of the triggering request |
| **`actionId`**             | string  | (Optionally) Idenfifier of the clicked action button |

## API

<docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### cancelNotification()

```typescript
cancelNotification() => any
```

Cancel the current notification

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(eventName: 'launch', listenerFunc: MessageListener) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Add a listener when the fullscreen intent launches the app.
You can navigate here to the destination page.
The parameter gives you the information if an action button has been clicked.

| Param              | Type                                    |
| ------------------ | --------------------------------------- |
| **`eventName`**    | <code>"launch"</code>                   |
| **`listenerFunc`** | <code>(response: any) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                      |
| ------------ | ------------------------- |
| **`remove`** | <code>() =&gt; any</code> |

</docgen-api>
