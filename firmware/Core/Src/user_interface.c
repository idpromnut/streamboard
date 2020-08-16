/*
 * user_interface.c
 *
 *  Created on: Aug 13, 2020
 *      Author: Chris
 */
#include <user_interface.h>

const ui_button_t  UI_BUTTON_1 = {.id=BUTTON_1, .in_gpio=SW1_IN_GPIO_Port, .in_gpio_pin=SW1_IN_Pin, .out_gpio=SW1_OUT_GPIO_Port, .out_gpio_pin=SW1_OUT_Pin};
const ui_button_t  UI_BUTTON_2 = {.id=BUTTON_2, .in_gpio=SW2_IN_GPIO_Port, .in_gpio_pin=SW2_IN_Pin, .out_gpio=SW2_OUT_GPIO_Port, .out_gpio_pin=SW2_OUT_Pin};
const ui_button_t  UI_BUTTON_3 = {.id=BUTTON_3, .in_gpio=SW3_IN_GPIO_Port, .in_gpio_pin=SW3_IN_Pin, .out_gpio=SW3_OUT_GPIO_Port, .out_gpio_pin=SW3_OUT_Pin};
const ui_button_t  UI_BUTTON_4 = {.id=BUTTON_4, .in_gpio=SW4_IN_GPIO_Port, .in_gpio_pin=SW4_IN_Pin, .out_gpio=SW4_OUT_GPIO_Port, .out_gpio_pin=SW4_OUT_Pin};
const ui_button_t  UI_BUTTON_5 = {.id=BUTTON_5, .in_gpio=SW5_IN_GPIO_Port, .in_gpio_pin=SW5_IN_Pin, .out_gpio=SW5_OUT_GPIO_Port, .out_gpio_pin=SW5_OUT_Pin};
const ui_button_t  UI_BUTTON_6 = {.id=BUTTON_6, .in_gpio=SW6_IN_GPIO_Port, .in_gpio_pin=SW6_IN_Pin, .out_gpio=SW6_OUT_GPIO_Port, .out_gpio_pin=SW6_OUT_Pin};
const ui_button_t  UI_BUTTON_7 = {.id=BUTTON_7, .in_gpio=SW7_IN_GPIO_Port, .in_gpio_pin=SW7_IN_Pin, .out_gpio=SW7_OUT_GPIO_Port, .out_gpio_pin=SW7_OUT_Pin};
const ui_button_t  UI_BUTTON_8 = {.id=BUTTON_8, .in_gpio=SW8_IN_GPIO_Port, .in_gpio_pin=SW8_IN_Pin, .out_gpio=SW8_OUT_GPIO_Port, .out_gpio_pin=SW8_OUT_Pin};
const ui_button_t  UI_BUTTON_9 = {.id=BUTTON_9, .in_gpio=SW9_IN_GPIO_Port, .in_gpio_pin=SW9_IN_Pin, .out_gpio=SW9_OUT_GPIO_Port, .out_gpio_pin=SW9_OUT_Pin};
const ui_button_t  UI_BUTTON_10 = {.id=BUTTON_10, .in_gpio=SW10_IN_GPIO_Port, .in_gpio_pin=SW10_IN_Pin, .out_gpio=SW10_OUT_GPIO_Port, .out_gpio_pin=SW10_OUT_Pin};

ui_button_t const * UI_BUTTONS[10] = {
		&UI_BUTTON_1,
		&UI_BUTTON_2,
		&UI_BUTTON_3,
		&UI_BUTTON_4,
		&UI_BUTTON_5,
		&UI_BUTTON_6,
		&UI_BUTTON_7,
		&UI_BUTTON_8,
		&UI_BUTTON_9,
		&UI_BUTTON_10
};

void ui_all_buttons_light_on(void)
{
	for(int i = 0; i < MAX_UI_BUTTONS; i++) HAL_GPIO_WritePin(UI_BUTTONS[i]->out_gpio, UI_BUTTONS[i]->out_gpio_pin, GPIO_PIN_SET);
}

void ui_all_buttons_light_off(void)
{
	for(int i = 0; i < MAX_UI_BUTTONS; i++) HAL_GPIO_WritePin(UI_BUTTONS[i]->out_gpio, UI_BUTTONS[i]->out_gpio_pin, GPIO_PIN_RESET);
}

void ui_button_set_light(ui_button_t * button, ui_button_light state)
{
	HAL_GPIO_WritePin(button->out_gpio, button->out_gpio_pin, state);
}

uint16_t ui_read_buttons(void)
{
	uint16_t result = 0;

	for(int i = 0; i < MAX_UI_BUTTONS; i++)
	{
		result |= (uint16_t)(HAL_GPIO_ReadPin(UI_BUTTONS[i]->in_gpio, UI_BUTTONS[i]->in_gpio_pin) << i);
	}
	return result;
}
