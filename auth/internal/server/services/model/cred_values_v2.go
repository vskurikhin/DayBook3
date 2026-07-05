package model

type CredValuesV2 struct {
	sessionID  SessionID
	timeTokens ValidTimeTokens
	user       User
}

func (c CredValuesV2) SessionID() SessionID {
	return c.sessionID
}

func (c CredValuesV2) TimeTokens() ValidTimeTokens {
	return c.timeTokens
}

func (c CredValuesV2) User() User {
	return c.user
}

func MakeCredValuesV2(sessionID SessionID, timeTokens ValidTimeTokens, user User) CredValuesV2 {
	return CredValuesV2{sessionID: sessionID, timeTokens: timeTokens, user: user}
}
