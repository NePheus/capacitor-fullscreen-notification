import { WebPlugin } from '@capacitor/core';

import { MessageListener, FullScreenNotificationPlugin } from './definitions';

export class FullScreenNotificationWeb
  extends WebPlugin
  implements FullScreenNotificationPlugin {
  cancelNotification(): Promise<void> {
    throw new Error('Method not implemented.');
  }
  addListener(eventName: 'launch', listenerFunc: MessageListener) {
    listenerFunc(null);
    return Promise.reject(
      `Method 'addListener' for event '${eventName}' not implemented.`,
    ) as any;
  }
}
