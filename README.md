[![npm version](https://badge.fury.io/js/capacitor-fullscreen-notification.svg)](https://badge.fury.io/js/capacitor-fullscreen-notification)
[![capacitor support](https://img.shields.io/badge/capacitor%20support-v3-brightgreen?logo=capacitor)](https://capacitorjs.com/)

# capacitor-fullscreen-notification

Start a fullscreen intent with a local notification triggered by a push notification.

The fullscreen notification will only be launched instantly, when you device is in standby. If not, you get a default heads up notification with the defined title, text and actionButtons.

To identify in your 'launch' listener, if the notification has been fullscreen started or by clicking the heads up notification, you can check the property 'isNotificationActive' in the response data. This is only true in the fullscreen notification, because by clicking the heads up notification, it gets removed by the system automatically.

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

## Dependencies

The fullscreen notification gets triggered by a firebase push notification, so you also have to install the package [@capacitor/push-notifications](https://github.com/ionic-team/capacitor-plugins/tree/main/push-notifications).

> **⚠ IMPORTANT**  
> For now, you have to install this version of @capacitor/push-notifications: **https://github.com/NePheus/capacitor-push-notifications/blob/master/README.md**
> See the top of the readme of this repository for the reason.

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

In you app, you can listen to the launch of the application and cancel the active notification:

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

---

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

---

### Interfaces

#### PluginListenerHandle

| Prop         | Type                      |
| ------------ | ------------------------- |
| **`remove`** | <code>() =&gt; any</code> |

</docgen-api>
