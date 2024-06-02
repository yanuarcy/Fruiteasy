import os
import numpy as np
from PIL import Image
from tensorflow.keras.preprocessing.image import ImageDataGenerator # type: ignore

# Define the path to the parent directory
parent_262_directory = './CombinedV5/262'       # parent direktori dari dataset
parent_360_directory = './CombinedV5/360'
tempor_folder = './Complete-ready-to-useV3/'    # parent direktori dari cleaning dataset
IMAGE_SIZE = 224

# List to store the directory names
path_262 = []   # list path dari direktori dataset 262 per buah
path_360 = []   # list path dari direktori dataset 360 per buah
fruit_name = []   # list dari nama buah yang ada di dataset

# Iterate through the parent 262 directory
for item in os.listdir(parent_262_directory):
    item_path = os.path.join(parent_262_directory, item)
    if os.path.isdir(item_path):
        path_262.append(item_path)
        fruit_name.append(item)

# Itearte through the parent 360 directory
for item in os.listdir(parent_360_directory):
    item_path = os.path.join(parent_360_directory, item)
    if os.path.isdir(item_path):
        path_360.append(item_path)

# prepare datagen generator
train_datagen = ImageDataGenerator(
    rotation_range=90,
    # width_shift_range=0.2,
    # height_shift_range=0.2,
    # shear_range=0.2,
    zoom_range=0.2,
    horizontal_flip=True,
    vertical_flip=True, 
    fill_mode='nearest',
    brightness_range=[0.8, 1.2],# Reasonable brightness range
    channel_shift_range=20.0,   # Moderate channel shift
)

# Fungsi untuk load images dari folder per buah di dataset
def load_images_from_folder(folder):
    images = []
    for filename in os.listdir(folder):
        if filename.endswith(('.jpg', '.jpeg', '.png')):
            img_path = os.path.join(folder, filename)
            try:
                img = Image.open(img_path)
                img = img.resize((IMAGE_SIZE, IMAGE_SIZE))  # Mengubah ukuran gambar menjadi 224X224
                img = img.convert('RGB')                    # Konversi gambar ke RGB 
                img_array = np.array(img) 
                images.append(img_array)
            except Exception as e:
                print(f'Error loading image {img_path}: {e}')
    return np.array(images)

def augment_images(images, n):
    augmented_images = []
    for idx, image in enumerate(images):
        try:
            print(f'Processing image {idx+1}/{len(images)}')
            if image.shape != (IMAGE_SIZE, IMAGE_SIZE, 3):
                print(f'Skipping image {idx+1} due to incorrect shape: {image.shape}')
                continue

            image = image.reshape((1, IMAGE_SIZE, IMAGE_SIZE, 3))
            i = 0
            for batch in train_datagen.flow(image, batch_size=1):
                augmented_images.append(batch[0])
                i += 1
                if i >= n:  # augmentasi n kali setiap gambar
                    break
            print(f'Image {idx+1} augmented {i} times')
        except Exception as e:
            print(f'Error processing image {idx+1}: {e}')

    augmented_images = np.array(augmented_images)
    return augmented_images

def save_images_to_local(output_folder, images, augment=False):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)
    
    for i, image in enumerate(images):
        img = Image.fromarray(image.astype(np.uint8))
        if augment==True:
            img_path = os.path.join(output_folder, f'augmented_image_{i}.jpg')
            img.save(img_path)
        else:
            img_path = os.path.join(output_folder, f'formatted_image_{i}.jpg')
            img.save(img_path)

# augment 262 
for dir, fruit in zip(path_262, fruit_name):
    images = load_images_from_folder(dir)
    augmented_images = augment_images(images, n=10)
    
    output_folder = os.path.join(tempor_folder, fruit)
    save_images_to_local(output_folder, augmented_images, augment=True)

# formatting 360
for dir, fruit in zip(path_360, fruit_name):
    images = load_images_from_folder(dir)
    output_folder = os.path.join(tempor_folder, fruit)
    print(output_folder)
    save_images_to_local(output_folder, images, augment=False)

    