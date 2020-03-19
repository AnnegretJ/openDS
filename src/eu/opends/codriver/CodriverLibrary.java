package eu.opends.codriver;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

import eu.opends.codriver.util.DataStructures.Input_data_str;
import eu.opends.codriver.util.DataStructures.Output_data_str;


public interface CodriverLibrary extends Library
{
	// Client init
	void client_codriver_init(String server_ip, int server_port, boolean log_enable, int log_type);

	// Client codriver compute
	void client_codriver_compute(Input_data_str scenario_msg, Output_data_str manoeuvre_msg);

	// Client codriver send
	int client_codriver_send(int server_run, int message_id, Input_data_str scenario_msg);

	// Client codriver receive
	int client_codriver_receive(Pointer server_run, Pointer message_id, Output_data_str manoeuvre_msg, long start_time);

	// Close socket
	void client_codriver_close();

	// Client Get time
	long client_get_time_ms();

	// Get motor cortex
	void get_flatten_motor_cortex(double[] motor_cortex);

	// get winning index j0 r0
    void get_winning_index(int[] index);

}    