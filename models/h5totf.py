from tensorflow import keras
model = keras.models.load_model('./mask_detector.model', compile=False)

export_path = './tf'
model.save(export_path, save_format="tf")