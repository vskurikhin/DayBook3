package tool

import "reflect"

// IsReallyNil checks whether the given interface value is truly nil.
//
// It correctly handles typed nil values (e.g., nil pointers, slices, maps, etc.)
// that are not equal to nil when stored in an interface.
func IsReallyNil(i interface{}) bool {
	if i == nil {
		return true
	}
	v := reflect.ValueOf(i)
	switch v.Kind() {
	case reflect.Chan, reflect.Func, reflect.Map, reflect.Pointer, reflect.UnsafePointer, reflect.Interface, reflect.Slice:
		return v.IsNil()
	default:
		return false
	}
}
