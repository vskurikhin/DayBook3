package tool

import "github.com/spf13/cobra"

func IsDebug(cmd *cobra.Command) bool {
	return cmd.Flag("debug").Value.String() == "true"
}

func IsVerbose(cmd *cobra.Command) bool {
	return cmd.Flag("verbose").Value.String() == "true"
}
