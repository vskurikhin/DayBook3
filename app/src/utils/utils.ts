export const STATUS = Object.freeze({
  IDLE: 'idle',
  PENDING: 'pending',
  SUCCEEDED: 'succeeded',
  FAILED: 'failed',
} as const);

export type Status = typeof STATUS[keyof typeof STATUS];