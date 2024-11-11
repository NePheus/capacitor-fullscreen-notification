import { WebPlugin } from '@capacitor/core';
import type { PluginListenerHandle } from '@capacitor/core';

import type {
  MessageListener,
  FullScreenNotificationPlugin,
} from './definitions';

export class FullScreenNotificationWeb
  extends WebPlugin
  implements FullScreenNotificationPlugin
{
  cancelNotification(): Promise<void> {
    throw new Error('Method not implemented.');
  }
  canUseFullScreenIntent(): Promise<{
    result: boolean;
  }> {
    throw new Error('Method not implemented.');
  }
  openFullScreenIntentSettings(): Promise<{
    result: boolean;
  }> {
    throw new Error('Method not implemented.');
  }
  addListener(
    eventName: 'launch',
    listenerFunc: MessageListener,
  ): Promise<PluginListenerHandle> {
    listenerFunc(null);
    return Promise.reject(
      `Method 'addListener' for event '${eventName}' not implemented.`,
    ) as any;
  }
}
