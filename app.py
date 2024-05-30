import csv
import numpy as np
from flask import Flask, request, jsonify, render_template, redirect, url_for
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import load_img
from tensorflow.keras.preprocessing.image import img_to_array
import PIL

app = Flask(__name__)

model = load_model('model/asuvskucing.h5')
# model = model.summary()

@app.route("/")
def root():
    return render_template('index.html')

@app.route('/predict', methods=['POST'])
def predict():
    imagefile = request.files['image']
    imagepath = "./images/" + imagefile.filename
    imagefile.save(imagepath)

    image = load_img(imagepath, target_size=(150, 150))
    x = img_to_array(image)
    x /= 255
    x = np.expand_dims(x, axis=0)
    images = np.vstack([x])
    classes = model.predict(images, batch_size=10)
    print(classes[0])

    if classes[0]>0.5:
        # print(image.filename + " is a dog")
        result = "anjing"
        url = url_for('animal_info', hewan=result)
        print(url)
        gas = redirect(url)
        print(gas)
        return gas
    else:
        # print(image.filename + " is a cat")
        result = "kucing"
        return redirect(url_for('animal_info', hewan=result))
    
    return jsonify({'prediction': result})

@app.route('/mengandung')
def animal_info():
    # mendapatkan parameter 'hewan' dari query string
    hewan = request.args.get('hewan')

    # mengecek apakah ada parameter yang dimasukkan 
    if not hewan:
        return jsonify({'error' : 'Harap Masukkan Data Yang Sesuai'}), 400
    
    # open and read hewan.csv
    with open('csv/hewan.csv', 'r', newline='', encoding='utf-8') as datahewan:
        reader = csv.DictReader(datahewan)
        # Iterasi untuk mencari data berdasarkan nama_hewan
        for row in reader:
            # jika hewan ditemukan pada hewan.csv maka return JSON
            if row['nama_hewan'].lower() == hewan.lower():
                return jsonify(row)
            
    # jika buah tidak ditemukan makan retirn error
    return jsonify({'error': 'Hewan tidak ditemukan'}), 404

# @app.route('/nutrisi')
# def nutrisi_buah():
#     # mendapatkan parameter 'buah' dari query string
#     buah = request.args.get('buah')

#     # mengecek apakah ada parameter yang dimasukkan 
#     if not buah:
#         return jsonify({'error' : 'Harap Masukkan Data Yang Sesuai'}), 400
    
#     # open and read data-buah.csv
#     with open('csv/data-buah.csv', 'r', newline='', encoding='utf-8') as databuah:
#         reader = csv.DictReader(databuah)
#         # Iterasi untuk mencari data berdasarkan nama_buah
#         for row in reader:
#             # jika buah ditemukan pada data-buah.csv maka return JSON
#             if row['nama_buah'].lower() == buah.lower():
#                 return jsonify(row)
            
#     # jika buah tidak ditemukan makan retirn error
#     return jsonify({'error': 'Buah tidak ditemukan'}), 404

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=3000, debug=True)