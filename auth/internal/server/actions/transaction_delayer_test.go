package actions

import (
	"context"
	"errors"
	"testing"

	"go.uber.org/mock/gomock"
)

func Test_deferTransaction(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	ctx := context.Background()

	txDelayer := TransactionDelayer{}

	tests := []struct {
		name        string
		tx          func() *MockTx
		err         error
		expectCalls func(tx *MockTx)
	}{
		{
			name: "error exists -> rollback success",
			tx: func() *MockTx {
				return NewMockTx(ctrl)
			},
			err: errors.New("some error"),
			expectCalls: func(tx *MockTx) {
				tx.EXPECT().Rollback(ctx).Return(nil)
			},
		},
		{
			name: "error exists -> rollback fails",
			tx: func() *MockTx {
				return NewMockTx(ctrl)
			},
			err: errors.New("some error"),
			expectCalls: func(tx *MockTx) {
				tx.EXPECT().Rollback(ctx).Return(errors.New("rollback error"))
			},
		},
		{
			name: "no error -> commit success",
			tx: func() *MockTx {
				return NewMockTx(ctrl)
			},
			err: nil,
			expectCalls: func(tx *MockTx) {
				tx.EXPECT().Commit(ctx).Return(nil)
			},
		},
		{
			name: "no error -> commit fails",
			tx: func() *MockTx {
				return NewMockTx(ctrl)
			},
			err: nil,
			expectCalls: func(tx *MockTx) {
				tx.EXPECT().Commit(ctx).Return(errors.New("commit error"))
			},
		},
		{
			name: "nil -> rollback success",
			tx: func() *MockTx {
				return nil
			},
			err:         nil,
			expectCalls: func(tx *MockTx) {},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tx := tt.tx()

			if tx != nil && tt.expectCalls != nil {
				tt.expectCalls(tx)
			}

			txDelayer.Defer(ctx, tx, tt.err)
		})
	}
}
