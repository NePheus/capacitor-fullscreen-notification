import { PluginListenerHandle } from '@capacitor/core';

export interface FullScreenNotificationPlugin {
  /**
   * Cancel the current notification
   */
  cancelNotification(): Promise<void>;

  /**
   * Add a listener when the fullscreen intent launches the app.
   * You can navigate here to the destination page.
   * The parameter gives you the information if an action button has been clicked.
   * @param eventName
   * @param listenerFunc
   */
  addListener(
    eventName: 'launch',
    listenerFunc: MessageListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
}

export type MessageListener = (response: any) => void;
