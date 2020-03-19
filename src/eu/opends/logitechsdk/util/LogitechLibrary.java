package eu.opends.logitechsdk.util;

import com.sun.jna.Library;
import com.sun.jna.ptr.IntByReference;

import eu.opends.logitechsdk.util.LogitechDataStructures.LogiControllerPropertiesData;
import eu.opends.logitechsdk.util.LogitechDataStructures.LogiControllerPropertiesData.LogiControllerPropertiesDataByReference;
import eu.opends.logitechsdk.util.LogitechDataStructures.LogiState;


public interface LogitechLibrary extends Library
{
	//Call this function before any other of the following
	boolean LogiSteeringInitialize(boolean ignoreXInputControllers);
	
	//Update the status of the controller
	boolean LogiUpdate();
	
	//Get the state of the controller in a struct
	LogiState LogiGetStateENGINES(int index);
	
	//Get the computer specific operating system assigned controller GUID at a given index
	boolean LogiGetDevicePath(int index, char[] str, int size);

	//Get the friendly name of the product at index
	boolean LogiGetFriendlyProductName(int index, char[] str, int size);

	//Check if a generic device at index is connected
	boolean LogiIsConnected(int index);

	//Check if the device connected at index is of the same type specified by deviceType
	boolean LogiIsDeviceConnected(int index, int deviceType);

	//Check if the device connected at index is made from the manufacturer specified by manufacturerName
	boolean LogiIsManufacturerConnected(int index, int manufacturerName);

	//Check if the device connected at index is the model specified by modelName
	boolean LogiIsModelConnected(int index, int modelName);

	//Check if the device connected at index is currently triggering the button specified by buttonNbr
	//TODO no reaction (however same in c# environment)
	boolean LogiButtonTriggered(int index, int buttonNbr);

	//Check if on the device connected at index has been released the button specified by buttonNbr
	//TODO no reaction (however same in c# environment)
	boolean LogiButtonReleased(int index, int buttonNbr);

	//Check if on the device connected at index is currently being pressed the button specified by buttonNbr 
	//TODO no reaction (however same in c# environment)
	boolean LogiButtonIsPressed(int index, int buttonNbr);

	//Generate non-linear values for the axis of the controller at index
	// index - index to which the concerned game controller is connected.
	// nonLinCoeff - value representing how much non-linearity should be applied. Range is -100 to 100.
	//    0 = linear curve, 
	//  100 = maximum non-linear curve with less sensitivity around center, 
	// -100 = maximum non-linearity with more sensitivity around center position.
	//TODO unexpected behavior (however same in c# environment)
	boolean LogiGenerateNonLinearValues(int index, int nonLinCoeff);

	//Get a non-linear value from a table previously generated
    // index - index of the game controller.  Index 0 corresponds to the
	// inputValue - value between -32768 and 32767 corresponding to original value of an axis.
	// RETURN VALUE - Value between -32768 and 32767 corresponding to the level of non-linearity previously 
	// set with GenerateNonLinearValues(...).
	//TODO unexpected behavior (however same in c# environment)
	int LogiGetNonLinearValue(int index, int inputValue);

	//Check if the controller at index has force feedback
	boolean LogiHasForceFeedback(int index);

	//Check if the controller at index is playing the force specified by forceType
	boolean LogiIsPlaying(int index, int forceType);

	//Play the spring force on the controller at index with the specified parameters
	// index - index of the game controller. 
	// offsetPercentage - Specifies the center of the spring force effect. Valid range is -100 to 100. 
	//     Specifying 0 centers the spring. Any values outside this range are silently clamped. 
	// saturationPercentage - Specify the level of saturation of the spring force effect. The saturation
	//     stays constant after a certain deflection from the center of the spring. It is comparable to a
	//     magnitude. Valid ranges are 0 to 100. Any value higher than 100 is silently clamped. 
	// coefficientPercentage - Specify the slope of the effect strength increase relative to the amount 
	//     of deflection from the center of the condition. Higher values mean that the saturation level is 
	//     reached sooner. Valid ranges are -100 to 100. Any value outside the valid range is silently clamped.
	boolean LogiPlaySpringForce(int index, int offsetPercentage, int saturationPercentage, int coefficientPercentage);

	//Stop the spring force on the controller at index
	boolean LogiStopSpringForce(int index);

	//Play the constant force on the controller at index with the specified parameter
	// index - index of the game controller. 
	// magnitudePercentage - Specifies the magnitude of the constant force effect. A negative value reverses 
	//     the direction of the force. Valid ranges for magnitudePercentage are -100 to 100. Any values outside
	//     the valid range are silently clamped.
	boolean LogiPlayConstantForce(int index, int magnitudePercentage);

	//Stop the constant force on the controller at index
	boolean LogiStopConstantForce(int index);

	//Play the damper force on the controller at index with the specified parameter
	// coefficientPercentage - specify the slope of the effect strength increase relative to the amount of 
	//     deflection from the center of the condition. Higher values mean that the saturation level is 
	//     reached sooner. Valid ranges are -100 to 100. Any value outside the valid range is silently clamped. 
	//     -100 simulates a very slippery effect, +100 makes the wheel/joystick very hard to move, simulating 
	//     the car at a stop or in mud.
	boolean LogiPlayDamperForce(int index, int coefficientPercentage);

	//Stop the damper force on the controller at index
	boolean LogiStopDamperForce(int index);

	//Play the side collision force on the controller at index with the specified parameter
	// index - index of the game controller. 
	// magnitudePercentage - Specifies the magnitude of the side collision force effect. A negative value reverses 
	//     the direction of the force. Valid ranges for magnitudePercentage are -100 to 100. Any values outside the valid 
	//     range are silently clamped.
	boolean LogiPlaySideCollisionForce(int index, int magnitudePercentage);

	//Play the frontal collision force on the controller at index with the specified parameter
	// index - index of the game controller. 
	// magnitudePercentage - specifies the magnitude of the frontal collision force effect. Valid ranges for 
	//     magnitudePercentage are 0 to 100. Values higher than 100 are silently clamped.
	boolean LogiPlayFrontalCollisionForce(int index, int magnitudePercentage);

	//Play the dirt road effect on the controller at index with the specified parameter
	// index - index of the game controller.
	// magnitudePercentage : Specifies the magnitude of the dirt road effect. Valid ranges for magnitudePercentage 
	//     are 0 to 100. Values higher than 100 are silently clamped.
	boolean LogiPlayDirtRoadEffect(int index, int magnitudePercentage);

	//Stop the dirt road effect on the controller at index
	boolean LogiStopDirtRoadEffect(int index);

	//Play the bumpy road effect on the controller at index with the specified parameter
	// index - index of the game controller.
	// magnitudePercentage : Specifies the magnitude of the bumpy road effect. Valid ranges for magnitudePercentage 
	//     are 0 to 100. Values higher than 100 are silently clamped.
	boolean LogiPlayBumpyRoadEffect(int index, int magnitudePercentage);

	//Stop the bumpy road effect on the controller at index
	boolean LogiStopBumpyRoadEffect(int index);

	//Play the slippery road effect on the controller at index with the specified parameter
	// index - index of the game controller.
	// magnitudePercentage : Specifies the magnitude of the slippery road effect. Valid ranges for magnitudePercentage 
	//     are 0 to 100. 100 corresponds to the most slippery effect.
	boolean LogiPlaySlipperyRoadEffect(int index, int magnitudePercentage);

	//Stop the slippery road effect on the controller at index
	boolean LogiStopSlipperyRoadEffect(int index);

	//Play the surface effect on the controller at index with the specified parameter
	// index - index of the game controller. 
	// type - Specifies the type of force effect. Can be one of the following values: LOGI_PERIODICTYPE_SINE, 
	//     LOGI_PERIODICTYPE_SQUARE, LOGI_PERIODICTYPE_TRIANGLE
	// magnitudePercentage - Specifies the magnitude of the surface effect. Valid ranges for magnitudePercentage 
	//     are 0 to 100. Values higher than 100 are silently clamped.
	// period - Specifies the period of the periodic force effect. The value is the duration for one full cycle of
	//     the periodic function measured in milliseconds. A good range of values for the period is 20 ms (sand) to 
	//     120 ms (wooden bridge or cobblestones). For a surface effect the period should not be any bigger than 150 ms.
	boolean LogiPlaySurfaceEffect(int index, int type, int magnitudePercentage, int period);

	//Stop the surface effect on the controller at index
	boolean LogiStopSurfaceEffect(int index);

	//Play the car airborne effect on the controller at index
	boolean LogiPlayCarAirborne(int index);

	//Stop the car airborne effect on the controller at index
	boolean LogiStopCarAirborne(int index);

	//Play the soft stop force on the controller at index with the specified parameter
	// index - index of the game controller. 
	// usableRangePercentage - specifies the deadband in percentage of the softstop force effect.
	boolean LogiPlaySoftstopForce(int index, int usableRangePercentage);

	//Stop the soft stop force on the controller at index
	boolean LogiStopSoftstopForce(int index);

	//Set preferred wheel properties specified by the struct properties
	boolean LogiSetPreferredControllerProperties(LogiControllerPropertiesData properties);

	//Fills the properties parameter with the current controller properties
	// NOTE: Function will fail and return default properties if user has older than 5.03 Logitech 
	//       Gaming Software installed.
	//TODO returns always default values (version 5.10.127) (however same in c# environment)
	boolean LogiGetCurrentControllerProperties(int index, LogiControllerPropertiesDataByReference properties);

	//get current shifter mode (gated or sequential)
	// RETURN VALUE: 1 if shifter is gated, 0 if shifter is sequential, -1 if unknown
	int LogiGetShifterMode(int index);

	//Sets the operating range in degrees on the controller at the index
	//TODO has no effect (however same in c# environment)
	boolean LogiSetOperatingRange(int index, int range);

	//Gets the current operating range in degrees on the controller at the index.
	//TODO has no effect (however same in c# environment)
	boolean LogiGetOperatingRange(int index, IntByReference range);

	//Play the leds on the controller at index applying the specified parameters.
	// index - index of the game controller
	// currentRPM - current RPM.
	// rpmFirstLedTurnsOn - RPM when first LEDs are to turn on.
	// rpmRedLine - just below this RPM, all LEDs will be on. Just above, all LEDs will start flashing.
	boolean LogiPlayLeds(int index, float currentRPM, float rpmFirstLedTurnsOn, float rpmRedLine);

	//Call this function to shutdown the SDK and destroy the controller and wheel objects
	void LogiSteeringShutdown();
}    