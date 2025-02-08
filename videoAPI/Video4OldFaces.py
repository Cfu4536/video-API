import json
import random
from flask import Flask, Response, request, abort,jsonify, send_file, render_template
from flask_cors import CORS
import socket
from moviepy.editor import VideoFileClip

from PIL import Image,ImageDraw
import os
import re

import pystray
from pystray import MenuItem, Menu
import threading
import psutil

app = Flask(__name__)
CORS(app)  # 启用跨域请求
VIDEO_FOLDER = 'D:\Files\oldfaces'  # 存放视频文件的文件夹
IMAGE_FOLDER = os.path.abspath('./tempPic') # 存放视频封面
INDEX_FOLDER = os.path.abspath('./templates')
APP_NAME = "Video4OldFaces.exe"

if not os.path.exists(IMAGE_FOLDER):#创建文件夹
        os.makedirs(IMAGE_FOLDER)
if not os.path.exists(VIDEO_FOLDER):#创建文件夹
        os.makedirs(VIDEO_FOLDER)
for file in os.listdir(IMAGE_FOLDER):#删除所有图片
        os.remove(os.path.join(IMAGE_FOLDER, file))

def init():
    def write_out_ip():
        """写入外网ip"""
        try:
            with open("./cache/ip.txt", "w") as f:
                s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
                # 连接到一个公网地址，这样可以获取本地的 IPv4 地址
                s.connect(("8.8.8.8", 80))  # Google 的公共DNS服务器
                ip_address = s.getsockname()[0]  # 获取本机的 IP 地址
                url = f"http://{ip_address}:12345"
                f.write(url)
                os.startfile(os.path.abspath("./cache/ip.txt"))
        except Exception:
            pass
    def create_image(width, height):
        # 创建一个简单的图标
        image = Image.new('RGB', (width, height), (255, 255, 255))
        dc = ImageDraw.Draw(image)
        dc.rectangle(
            (0, 0, width, height),
            fill=(0, 128, 255),
            outline=(0, 0, 0)
        )
        return image
    def on_quit(icon, item):
        for proc in psutil.process_iter(['pid', 'name']):
            try:
                if APP_NAME.lower() in proc.info['name'].lower():
                    print(f"Killing process: {proc.info['name']} (PID: {proc.info['pid']})")
                    proc.kill()
            except (psutil.NoSuchProcess, psutil.AccessDenied, psutil.ZombieProcess):
                pass
    # 获取本机IP
    write_out_ip()
    # 创建系统托盘图标
    image = create_image(64, 64)
    menu = Menu(
        MenuItem('Quit', on_quit),
    )
    icon = pystray.Icon("test_icon", image, "Test Icon", menu)
    # 运行托盘图标
    icon.run()
    

@app.route('/')
def index():
    print("工作路径："+os.getcwd())
    # return render_template('index.html')
    return send_file(os.path.join(INDEX_FOLDER,"index.html"))



def generate_video_stream(video_path, start, end):
    """生成视频文件流，支持范围请求"""
    with open(video_path, 'rb') as f:
        f.seek(start)  # 跳转到开始位置
        bytes_to_read = end - start + 1
        while bytes_to_read > 0:
            chunk = f.read(min(1024 * 512, bytes_to_read))  # 最多读取1MB的块
            if not chunk:
                break
            yield chunk
            bytes_to_read -= len(chunk)

@app.route('/get_video')
def get_video():
    """获取视频文件，进行简单流式传输"""
    file_path = request.args.get('file_path')
    if not file_path:
        return "Filename is required", 400

    video_path = os.path.join(VIDEO_FOLDER, file_path)
    if not os.path.exists(video_path):
        abort(404)

    # 处理 Range 请求
    range_header = request.headers.get('Range', None)
    if not range_header:
        return Response(generate_video_stream(video_path, 0, os.path.getsize(video_path) - 1),
                       mimetype='video/mp4')

    # 解析 Range 头
    match = re.match(r'bytes=(\d+)-(\d*)', range_header)
    if not match:
        return "Invalid Range Header", 416

    start = int(match.group(1))
    end = int(match.group(2)) if match.group(2) else os.path.getsize(video_path) - 1

    # 设置正确的响应头
    response = Response(generate_video_stream(video_path, start, end),
                       status=206,  # Partial Content
                       mimetype='video/mp4')
    response.headers.add('Content-Range', f'bytes {start}-{end}/{os.path.getsize(video_path)}')
    response.headers.add('Accept-Ranges', 'bytes')
    
    return response

@app.route('/delete_video',methods=['DELETE','GET'])
def delete_video():
    """删除指定的视频文件"""
    file_path = request.args.get('file_path')
    if not file_path:
        return "Filename is required", 400

    video_path = os.path.join(VIDEO_FOLDER, file_path)
    if not os.path.exists(video_path):
        return "File not found", 404

    try:
        os.remove(video_path)  # 删除文件
        return f"File '{file_path}' has been deleted", 200
    except Exception as e:
        return f"Error deleting file: {str(e)}", 500


def generate_video_img(file_list):
    """生成视频封面"""
    for video_path in file_list:#生成封面
        img_path = os.path.join(IMAGE_FOLDER, os.path.basename(video_path))
        if not os.path.exists(img_path):
            with VideoFileClip(video_path) as video:
                # video = VideoFileClip(video_path)
                video.save_frame(img_path+".png", t=0)
                # 使用PIL调整图像质量
            with Image.open(img_path+".png") as img:
                img.save(img_path, 'JPEG', quality=60)  # 设定质量
                os.remove(img_path+".png")  # 删除临时帧
    
# def generate_video_img(file_list):
#     """生成视频封面，随机抽取一帧"""
#     for video_path in file_list:
#         img_path = os.path.join(IMAGE_FOLDER, os.path.basename(video_path))
#         if not os.path.exists(img_path):
#             video = VideoFileClip(video_path)
#             # 随机选择一个时间点
#             random_time = random.uniform(0, video.duration)
#             video.save_frame(img_path + ".png", t=random_time)
#             # 使用PIL调整图像质量
#             with Image.open(img_path + ".png") as img:
#                img.save(img_path, 'JPEG', quality=60)  # 设定质量
#             os.remove(img_path + ".png")  # 删除临时帧

                
@app.route('/get_image')
def get_image():
    # 返回图片文件
    img_path = request.args.get('img_path')
    if not img_path:
        return "Filename is required", 400

    img_path = os.path.join(VIDEO_FOLDER, img_path)
    if not os.path.exists(img_path):
        abort(404)
        
    return send_file(img_path, mimetype='image/jpeg')

@app.route('/get_video_img')
def get_video_img():
    # 返回视频缩略图
    video_path = request.args.get('video_path')
    if not video_path:
        return "Filename is required", 400

    video_path = os.path.join(VIDEO_FOLDER, video_path)
    if not os.path.exists(video_path):
        abort(404)
    
    img_path = os.path.join(IMAGE_FOLDER, os.path.basename(video_path))
    generate_video_img(file_list=[video_path])    
    return send_file(img_path, mimetype='image/jpeg')



def get_folder_size(folder_path):
    """获取文件夹大小"""
    def save_cache_to_file(cache_file='./cache/folder_size_cache.json'):
        """将缓存的文件夹大小写入 JSON 文件"""
        with open(cache_file, 'w') as json_file:
            json.dump(folder_size_cache, json_file, indent=4)

    def load_cache_from_file(cache_file='./cache/folder_size_cache.json'):
        """从 JSON 文件加载缓存的文件夹大小"""
        if os.path.exists(cache_file):
            with open(cache_file, 'r') as json_file:
                return json.load(json_file)
    
    folder_size_cache = load_cache_from_file()#加载缓存

    if folder_path in folder_size_cache:
        return folder_size_cache[folder_path]
    
    total_size = 0
    for root, dirs, files in os.walk(folder_path):
        for file in files:
            file_path = os.path.join(root, file)
            total_size += os.path.getsize(file_path)
            
    # 将计算结果缓存起来
    folder_size_cache[folder_path] = total_size
    save_cache_to_file()  # 保存缓存到文件
    return total_size


@app.route('/get_video_list')
def get_video_list():
    """查询文件列表"""
    # http://172.20.10.2:12345/get_video_list?folder_path=唐迟""

    folder_path = request.args.get('folder_path')  # 从查询参数中获取文件名
    if not folder_path:
        folder_path=""

    full_folder_path = os.path.join(VIDEO_FOLDER, folder_path)

    # 检查目录是否存在
    if not os.path.exists(full_folder_path):
        return "Folder not found", 404

    try:
        all_file_list = os.listdir(full_folder_path)
        file_list = [file for file in all_file_list if file.endswith('.cfu') or file.lower().endswith(('.jpg', '.jpeg', '.png', '.gif')) or "." not in file]
        type_list = [
            "dir" if os.path.isdir(os.path.join(full_folder_path, file)) else
            "on-video" if file.endswith('.cfu') else "on-picture"
            for file in file_list
        ]
        size_list = [
            os.path.getsize(os.path.join(full_folder_path, file))//(1024*1024) if os.path.isfile(os.path.join(full_folder_path, file)) 
            else get_folder_size(os.path.join(full_folder_path, file))//(1024*1024) for file in file_list
        ]
        # generate_video_img([os.path.join(full_folder_path,file) for file in all_file_list if file.endswith('.cfu')])#生成封面
    except Exception as e:
        return str(e), 500

    file_dict = {
        "parent": os.path.basename(full_folder_path),
        "children": file_list,
        "type": type_list,
        "size":size_list
    }

    # 以 JSON 格式返回字典
    return jsonify(file_dict)
    
  
if __name__ == '__main__':
    tray_thread = threading.Thread(target=init).start() 
    app.run(host='0.0.0.0', port=12345,debug=True)
