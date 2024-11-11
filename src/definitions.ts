import { PluginListenerHandle } from '@capacitor/core';

export interface FullScreenNotificationPlugin {
  /**
   * Cancel the current notification
   */
  cancelNotification(): Promise<void>;

  /**
   * Check if the user granted permission to show full screen intents
   */
  canUseFullScreenIntent(): Promise<{
    result: boolean;
  }>;

  /**
   * Open the settings page to grant the full screen permission
   */
  openFullScreenIntentSettings(): Promise<{
    result: boolean;
  }>;

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
  ): Promise<PluginListenerHandle>;

  /**
   * Removes all listeners.
   */
  removeAllListeners(): Promise<void>;
}

export type MessageListener = (response: any) => void;
