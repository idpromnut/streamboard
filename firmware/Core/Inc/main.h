/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.h
  * @brief          : Header for main.c file.
  *                   This file contains the common defines of the application.
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; Copyright (c) 2020 STMicroelectronics.
  * All rights reserved.</center></h2>
  *
  * This software component is licensed by ST under BSD 3-Clause license,
  * the "License"; You may not use this file except in compliance with the
  * License. You may obtain a copy of the License at:
  *                        opensource.org/licenses/BSD-3-Clause
  *
  ******************************************************************************
  */
/* USER CODE END Header */

/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __MAIN_H
#define __MAIN_H

#ifdef __cplusplus
extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "stm32f1xx_hal.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */

/* USER CODE END Includes */

/* Exported types ------------------------------------------------------------*/
/* USER CODE BEGIN ET */

/* USER CODE END ET */

/* Exported constants --------------------------------------------------------*/
/* USER CODE BEGIN EC */

/* USER CODE END EC */

/* Exported macro ------------------------------------------------------------*/
/* USER CODE BEGIN EM */

/* USER CODE END EM */

/* Exported functions prototypes ---------------------------------------------*/
void Error_Handler(void);

/* USER CODE BEGIN EFP */

/* USER CODE END EFP */

/* Private defines -----------------------------------------------------------*/
#define SW7_IN_Pin GPIO_PIN_0
#define SW7_IN_GPIO_Port GPIOA
#define SW8_IN_Pin GPIO_PIN_1
#define SW8_IN_GPIO_Port GPIOA
#define SW9_IN_Pin GPIO_PIN_2
#define SW9_IN_GPIO_Port GPIOA
#define SW10_IN_Pin GPIO_PIN_3
#define SW10_IN_GPIO_Port GPIOA
#define SW1_OUT_Pin GPIO_PIN_0
#define SW1_OUT_GPIO_Port GPIOB
#define SW2_OUT_Pin GPIO_PIN_1
#define SW2_OUT_GPIO_Port GPIOB
#define SW3_OUT_Pin GPIO_PIN_2
#define SW3_OUT_GPIO_Port GPIOB
#define SW1_IN_Pin GPIO_PIN_10
#define SW1_IN_GPIO_Port GPIOB
#define SW2_IN_Pin GPIO_PIN_11
#define SW2_IN_GPIO_Port GPIOB
#define SW3_IN_Pin GPIO_PIN_12
#define SW3_IN_GPIO_Port GPIOB
#define SW4_IN_Pin GPIO_PIN_13
#define SW4_IN_GPIO_Port GPIOB
#define SW5_IN_Pin GPIO_PIN_14
#define SW5_IN_GPIO_Port GPIOB
#define SW6_IN_Pin GPIO_PIN_15
#define SW6_IN_GPIO_Port GPIOB
#define USB_EN_Pin GPIO_PIN_8
#define USB_EN_GPIO_Port GPIOA
#define SW4_OUT_Pin GPIO_PIN_3
#define SW4_OUT_GPIO_Port GPIOB
#define SW5_OUT_Pin GPIO_PIN_4
#define SW5_OUT_GPIO_Port GPIOB
#define SW6_OUT_Pin GPIO_PIN_5
#define SW6_OUT_GPIO_Port GPIOB
#define SW7_OUT_Pin GPIO_PIN_6
#define SW7_OUT_GPIO_Port GPIOB
#define SW8_OUT_Pin GPIO_PIN_7
#define SW8_OUT_GPIO_Port GPIOB
#define SW9_OUT_Pin GPIO_PIN_8
#define SW9_OUT_GPIO_Port GPIOB
#define SW10_OUT_Pin GPIO_PIN_9
#define SW10_OUT_GPIO_Port GPIOB
/* USER CODE BEGIN Private defines */

/* USER CODE END Private defines */

#ifdef __cplusplus
}
#endif

#endif /* __MAIN_H */

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
