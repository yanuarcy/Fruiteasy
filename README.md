# Fruiteasy
## Overview
Fruiteasy, an innovative Android application that uses advanced image classification technology to help you make informed fruit choices. Simply capture an image of a fruit, and our app will identify it and provide detailed nutritional information, serving methods, dietary benefits, and potential health benefits.

This branch is the result of the work from Fruiteasy ML team. We use Tensorflow version 2.15.0 and build our model using Google Colab. We obtain our dataset from kaggle which processed to get <a href="https://github.com/yanuarcy/Fruiteasy/blob/ML/Assets/dataset-and-pretrain.txt">this result</a>. We also apply transfer learning by using pretrained model <a href="https://www.tensorflow.org/api_docs/python/tf/keras/applications/MobileNetV2">MobileNetV2</a> which has available on keras. Our objective is to build a fruit classification ML model that can identify up to 20 different type of fruits.

This repository is public and is intended for the Capstone Project Bangkit Academy Batch 6. 

## Prerequisites
* Python 3.10.12
* Tensorflow 2.15.0
* Install necessary packages using `pip install -r requirements.txt`

## Our Result
| Resources  | Description |  Link URL | 
| :-------------: | :---------------: | :---------------: |
| fruiteasyV6.h5  | Our latest trained model | [Link](https://drive.google.com/file/d/1B7Jp0Wf-B1fLnvJmdDnVvbIcyd_f0WtG/view?usp=sharing)  |
| CombinedV6.zip  | Our latest raw dataset | [Link](https://drive.google.com/file/d/1KaIHEjQgJUgRcU6YfF_wevzsw2VYvTUI/view?usp=sharing) |
| Complete-ready-to-useV4.zip  | Our latest cleaned/preprocessed/formatted/augmented dataset | [Link](https://drive.google.com/file/d/1bnnz-AsxpF74U6WouIYl70Nb-nrVr4Io/view?usp=sharing) |
| 262-for-testV2.zip  | Our latest dataset for testing | [Link](https://drive.google.com/file/d/1-IwP2zJb0WK_d4DKeU5ZEEux4VTCucW9/view?usp=sharing) |

## Preprocess raw dataset
We already provide a code to preprocess/format/augment our raw dataset (our dataset available on `/Assets/dataset-and-pretrain.txt`)

* If you want to use Google Colab/Jupyter Notebook, simply upload the `Preprocessing_formatting_augmenting.ipynb`, or use our colab <a href="https://colab.research.google.com/drive/1C1RIy1sKiEEwBO-XvN8gWPqROaf0R8Qd?usp=sharing">Here</a>. Just run all the cells and modify some variable name if you want.
* If you are using linux and want to use standard python code, follow this steps:
1. Download one of the raw dataset in here `/Assets/dataset-and-pretrain.txt`, the raw dataset indicated by name `CombinedV`
2. Extract the dataset
3. simply run `python3 Preprocessing-alternative-code.py`
4. zip the file and use it for next step

## Training-evaluation-prediction
| 📰 Preprocessing  | 📔 Train-eval-predict | 
| :-------------: | :---------------: |
| [Link](https://colab.research.google.com/drive/1C1RIy1sKiEEwBO-XvN8gWPqROaf0R8Qd?usp=sharing) | [Link](https://colab.research.google.com/drive/1YW9y0KYtM2gTJbrOujV1OeerVadrGqvt?usp=sharing)  |

Here is a couple notes:
- Make sure T4 Gpu selected on runtime for better performance
- Run the dependencies cell
- After that you can either go for training process (A) or simply skip it to use our model directly (B)
- If you choose option A, you need to run all the cells from 1 to 5, then run either option with fine tuning or without fine tuning (please choose one), next start the training loop using 6, for the rest 7-10 you can simply explore it (self explanatory)
- If you choose option B, you can directly use the model to predict after running the 1 and 2.


