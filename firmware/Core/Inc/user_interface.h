/*
 * user_interface.h
 *
 *  Created on: Aug 13, 2020
 *      Author: Chris
 */

#ifndef INC_USER_INTERFACE_H_
#define INC_USER_INTERFACE_H_

#include "main.h"

typedef enum {
	BUTTON_1 	= 0x0001,
	BUTTON_2 	= 0x0002,
	BUTTON_3 	= 0x0004,
	BUTTON_4 	= 0x0008,
	BUTTON_5 	= 0x0010,
	BUTTON_6 	= 0x0020,
	BUTTON_7 	= 0x0040,
	BUTTON_8 	= 0x0080,
	BUTTON_9 	= 0x0100,
	BUTTON_10 = 0x0200,
} ui_button_id;

typedef struct {
	ui_button_id id;
	GPIO_TypeDef * in_gpio;
	uint16_t in_gpio_pin;
	GPIO_TypeDef * out_gpio;
	uint16_t out_gpio_pin;
} ui_button_t;

typedef enum {
	ON = GPIO_PIN_SET,
	OFF = GPIO_PIN_RESET
} ui_button_light;

#define MAX_UI_BUTTONS  (10)

#define WHITE_BUTTON_1 	((ui_button_t *)&UI_BUTTON_1)
#define WHITE_BUTTON_2 	((ui_button_t *)&UI_BUTTON_2)
#define WHITE_BUTTON_3 	((ui_button_t *)&UI_BUTTON_3)
#define WHITE_BUTTON_4 	((ui_button_t *)&UI_BUTTON_4)
#define BLUE_BUTTON_1 	((ui_button_t *)&UI_BUTTON_5)
#define BLUE_BUTTON_2 	((ui_button_t *)&UI_BUTTON_6)
#define BLUE_BUTTON_3 	((ui_button_t *)&UI_BUTTON_7)
#define GREEN_BUTTON_1 	((ui_button_t *)&UI_BUTTON_8)
#define AMBER_BUTTON_1 	((ui_button_t *)&UI_BUTTON_9)
#define AMBER_BUTTON_2 	((ui_button_t *)&UI_BUTTON_10)

void ui_all_buttons_light_on(void);
void ui_all_buttons_light_off(void);
void ui_button_set_light(ui_button_t * button, ui_button_light state);
uint16_t ui_read_buttons(void);

#endif /* INC_USER_INTERFACE_H_ */
