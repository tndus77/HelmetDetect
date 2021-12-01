import os
import sys
import random
import argparse
import numpy as np
from PIL import Image, ImageFile

__version__ = '0.3.0'


IMAGE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'images')
IMAGE1_PATH = os.path.join(IMAGE_DIR, 'helmet1.jpg')
IMAGE2_PATH = os.path.join(IMAGE_DIR, 'helmet2.jpg')
IMAGE3_PATH = os.path.join(IMAGE_DIR, 'helmet3.jpg')
IMAGE4_PATH = os.path.join(IMAGE_DIR, 'helmet4.jpg')


def cli():
    parser = argparse.ArgumentParser(description='Wear a face helmet in the given picture.')
    parser.add_argument('pic_path', help='Picture path.')
    parser.add_argument('--show', action='store_true', help='Whether show picture with helmet or not.')
    parser.add_argument('--model', default='hog', choices=['hog', 'cnn'], help='Which face detection model to use.')
    group = parser.add_mutually_exclusive_group()
    group.add_argument('--one', action='store_true', help='Wear helmet1')
    group.add_argument('--two', action='store_true', help='Wear helmet2')
    group.add_argument('--three', action='store_true', help='Wear helmet3')
    group.add_argument('--four', action='store_true', help='Wear helmet4')
    group.add_argument('--five', action='store_true', help='Wear helmet5')
    args = parser.parse_args()

    pic_path = args.pic_path
    if not os.path.exists(args.pic_path):
        print(f'Picture {pic_path} not exists.')
        sys.exit(1)

    if args.one:
        helmet_path = IMAGE1_PATH
    elif args.two:
        helmet_path = IMAGE2_PATH
    elif args.three:
        helmet_path = IMAGE3_PATH
    elif args.four:
        helmet_path = IMAGE4_PATH
    else:
        helmet_path = IMAGE5_PATH

    FaceMasker(pic_path, helmet_path, args.show, args.model).helmet()


def create_helmet(image_path):
    pic_path = image_path
    helmet_path = os.path.join(IMAGE_DIR, 'helmet6.png')
    show = False
    model = "hog"
    FaceMasker(pic_path, helmet_path, show, model).helmet()



class FaceMasker:
    KEY_FACIAL_FEATURES = ('left_eye', 'right_eye')

    def __init__(self, face_path, helmet_path, show=False, model='hog'):
        self.face_path = face_path
        self.helmet_path = helmet_path
        self.show = show
        self.model = model
        self._face_img: ImageFile = None
        self._helmet_img: ImageFile = None

    def helmet(self):
        import face_recognition

        face_image_np = face_recognition.load_image_file(self.face_path)
        face_locations = face_recognition.face_locations(face_image_np, model=self.model)
        #face_landmarks = face_recognition.face_landmarks(face_image_np, face_locations)
        self._face_img = Image.fromarray(face_image_np)
        self._helmet_img = Image.open(self.helmet_path)

        found_face = False
        for face_location in face_locations:
            face_landmarks = face_recognition.face_landmarks(face_image_np, face_locations)
            # check whether facial features meet requirement
            for face_landmark in face_landmarks:
                skip = False
                for facial_feature in self.KEY_FACIAL_FEATURES:
                    if facial_feature not in face_landmark:
                        skip = True
                        break
                    if skip:
                        continue

            # helmet face
            found_face = True
            self._helmet_face(face_landmark, face_location)

        if found_face:
            if self.show:
                self._face_img.show()

            # save
            self._save()
        else:
            print('Found no face.')

    def _helmet_face(self, face_landmark: dict, face_location):
        top, right, bottom, left = face_location

        left_eye = face_landmark['left_eye']
        left_eye_point = left_eye[len(left_eye) // 2]
        left_eye_v = np.array(left_eye_point)

        right_eye = face_landmark['right_eye']
        right_eye_point = right_eye[len(right_eye) // 2]
        right_eye_v = np.array(right_eye_point)

        eye_v = (right_eye_v + left_eye_v)//2

        # split helmet and resize
        width = self._helmet_img.width
        height = self._helmet_img.height
        width_ratio = 1.2
        new_height = int(np.linalg.norm(top - eye_v[1]))
        new_width = int(np.linalg.norm(right-left))

        # left
        helmet_left_img = self._helmet_img.crop((0, 0, width // 2, height))
        #helmet_left_width = self.get_distance_from_point_to_line(chin_left_point, nose_point, chin_bottom_point)
        #helmet_left_width = int(helmet_left_width * width_ratio)
        #helmet_left_img = helmet_left_img.resize((helmet_left_width, new_height))

        # right
        #helmet_right_img = self._helmet_img.crop((width // 2, 0, width, height))
        #helmet_right_width = self.get_distance_from_point_to_line(chin_right_point, nose_point, chin_bottom_point)
        #helmet_right_width = int(helmet_right_width * width_ratio)
        #helmet_right_img = helmet_right_img.resize((helmet_right_width, new_height))


        # merge helmet
        size = (new_width, new_height*2)
        new_helmet  = self._helmet_img.resize(size)
        #self._helmet_img = Image.new('RGBA', size)
        #helmet_img.paste(helmet_img, (0, 0), helmet_img)

        #여기서부터 임의로 부착
        #b = os.path.basename(self.face_path)
        #pname = os.path.splitext(b)
        #temp_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'with_helmet\\') + pname[0] + '-with-helmet' + pname[1]
        #self._face_img.paste(new_helmet, (0,0))
        #self._face_img.save(temp_path)
        #여기까지 부착 끝

        # rotate helmet
        angle = np.arctan2(top - (left_eye_point[1] + right_eye_point[1]) / 2, (left+right)/2 - (left_eye_point[0] + right_eye_point[0])/2)
        rotated_helmet_img = self._helmet_img.rotate(angle, expand=True)

        # calculate helmet location
        center_x = ((left+right)/2 + (left_eye_point[0] + right_eye_point[0]) / 2) // 2
        center_y = (top + (left_eye_point[1] + right_eye_point[1]) / 2) // 2

        offset = self._helmet_img.width // 2 - helmet_left_img.width
        radian = angle * np.pi / 180
        box_x = int(center_x + int(offset * np.cos(radian)) - rotated_helmet_img.width // 2)
        box_y = int(center_y + int(offset * np.sin(radian)) - rotated_helmet_img.height // 2)

        # add helmet
        #self._face_img.paste(helmet_img, (box_x, box_y), helmet_img)
        #self._face_img.paste(new_helmet, (left, top-self._helmet_img.height), new_helmet)
        self._face_img.paste(new_helmet, (left, top-self._helmet_img.height//3), new_helmet)

    def _save(self):
        path_splits = os.path.splitext(self.face_path)
        base = os.path.basename(self.face_path)
        pure_name = os.path.splitext(base)
        new_face_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'with_helmet4\\') + pure_name[0] + '-with-helmet6-1' + pure_name[1]
        self._face_img.save(new_face_path)
        print(f'Save to {new_face_path}')

    @staticmethod
    def get_distance_from_point_to_line(point, line_point1, line_point2):
        distance = np.abs((line_point2[1] - line_point1[1]) * point[0] +
                          (line_point1[0] - line_point2[0]) * point[1] +
                          (line_point2[0] - line_point1[0]) * line_point1[1] +
                          (line_point1[1] - line_point2[1]) * line_point1[0]) / \
                   np.sqrt((line_point2[1] - line_point1[1]) * (line_point2[1] - line_point1[1]) +
                           (line_point1[0] - line_point2[0]) * (line_point1[0] - line_point2[0]))
        return int(distance)


if __name__ == '__main__':
    #cli()
    create_helmet(image_path)
