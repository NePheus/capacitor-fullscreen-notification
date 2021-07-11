import { registerPlugin } from '@capacitor/core';

import type { FullScreenNotificationPlugin } from './definitions';

const FullScreenNotification = registerPlugin<FullScreenNotificationPlugin>(
  'FullScreenNotification',
  {
    web: () => import('./web').then(m => new m.FullScreenNotificationWeb()),
  },
);

export * from './definitions';
export { FullScreenNotification };
