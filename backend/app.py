from flask import Flask, request, jsonify
from flask_cors import CORS
from datetime import datetime, timedelta
import sqlite3
import jwt
import hashlib
import json
from functools import wraps

app = Flask(__name__)
CORS(app)
app.config['SECRET_KEY'] = 'your-secret-key-here'

# 数据库初始化
def init_db():
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()

    # 用户表
    c.execute('''CREATE TABLE IF NOT EXISTS users
                 (id INTEGER PRIMARY KEY AUTOINCREMENT,
                  username TEXT UNIQUE NOT NULL,
                  password TEXT NOT NULL,
                  email TEXT,
                  gender TEXT,
                  age INTEGER,
                  height REAL,
                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)''')

    # 身体数据表
    c.execute('''CREATE TABLE IF NOT EXISTS body_data
                 (id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER,
                  weight REAL,
                  body_fat REAL,
                  bmi REAL,
                  record_date DATE,
                  FOREIGN KEY (user_id) REFERENCES users(id))''')

    # 健身计划表
    c.execute('''CREATE TABLE IF NOT EXISTS fitness_plans
                 (id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER,
                  plan_name TEXT,
                  goal TEXT,
                  difficulty TEXT,
                  duration INTEGER,
                  description TEXT,
                  plan_content TEXT,
                  is_ai_generated BOOLEAN,
                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY (user_id) REFERENCES users(id))''')

    # 训练记录表
    c.execute('''CREATE TABLE IF NOT EXISTS training_records
                 (id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER,
                  plan_id INTEGER,
                  exercise_name TEXT,
                  duration INTEGER,
                  calories REAL,
                  notes TEXT,
                  record_date DATE,
                  FOREIGN KEY (user_id) REFERENCES users(id),
                  FOREIGN KEY (plan_id) REFERENCES fitness_plans(id))''')

    # 营养记录表
    c.execute('''CREATE TABLE IF NOT EXISTS nutrition_records
                 (id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER,
                  meal_type TEXT,
                  food_name TEXT,
                  calories REAL,
                  protein REAL,
                  carbs REAL,
                  fat REAL,
                  record_date DATE,
                  FOREIGN KEY (user_id) REFERENCES users(id))''')

    # 社区动态表
    c.execute('''CREATE TABLE IF NOT EXISTS community_posts
                 (id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER,
                  content TEXT,
                  image_url TEXT,
                  likes INTEGER DEFAULT 0,
                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY (user_id) REFERENCES users(id))''')

    # 点赞记录表
    c.execute('''CREATE TABLE IF NOT EXISTS post_likes
                 (id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER,
                  post_id INTEGER,
                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  UNIQUE(user_id, post_id),
                  FOREIGN KEY (user_id) REFERENCES users(id),
                  FOREIGN KEY (post_id) REFERENCES community_posts(id))''')

    # 评论表
    c.execute('''CREATE TABLE IF NOT EXISTS post_comments
                 (id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER,
                  post_id INTEGER,
                  content TEXT,
                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY (user_id) REFERENCES users(id),
                  FOREIGN KEY (post_id) REFERENCES community_posts(id))''')

    conn.commit()
    conn.close()

# JWT认证装饰器
def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.headers.get('Authorization')
        if not token:
            return jsonify({'message': '缺少认证令牌'}), 401

        try:
            if token.startswith('Bearer '):
                token = token[7:]
            data = jwt.decode(token, app.config['SECRET_KEY'], algorithms=["HS256"])
            current_user_id = data['user_id']
        except jwt.ExpiredSignatureError:
            return jsonify({'message': '认证令牌已过期，请重新登录'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'message': '无效的认证令牌'}), 401
        except Exception as e:
            print(f'Token验证错误: {str(e)}')
            return jsonify({'message': '认证失败'}), 401

        return f(current_user_id, *args, **kwargs)

    return decorated

# 用户注册
@app.route('/api/register', methods=['POST'])
def register():
    try:
        data = request.json
        username = data.get('username')
        password = data.get('password')
        email = data.get('email')

        # 参数验证
        if not username or not password:
            return jsonify({'message': '用户名和密码不能为空'}), 400

        if len(username) < 3 or len(username) > 20:
            return jsonify({'message': '用户名长度应为3-20个字符'}), 400

        if len(password) < 6:
            return jsonify({'message': '密码长度不能少于6个字符'}), 400

        # 密码加密
        hashed_password = hashlib.sha256(password.encode()).hexdigest()

        conn = sqlite3.connect('fitness.db')
        c = conn.cursor()
        c.execute('INSERT INTO users (username, password, email) VALUES (?, ?, ?)',
                  (username, hashed_password, email))
        conn.commit()
        user_id = c.lastrowid
        conn.close()

        return jsonify({'message': '注册成功', 'user_id': user_id}), 201
    except sqlite3.IntegrityError:
        return jsonify({'message': '用户名已存在'}), 400
    except Exception as e:
        print(f'注册错误: {str(e)}')
        return jsonify({'message': '注册失败，请稍后重试'}), 500

# 用户登录
@app.route('/api/login', methods=['POST'])
def login():
    try:
        data = request.json
        username = data.get('username')
        password = data.get('password')

        if not username or not password:
            return jsonify({'message': '用户名和密码不能为空'}), 400

        hashed_password = hashlib.sha256(password.encode()).hexdigest()

        conn = sqlite3.connect('fitness.db')
        c = conn.cursor()
        c.execute('SELECT id, username FROM users WHERE username = ? AND password = ?',
                  (username, hashed_password))
        user = c.fetchone()
        conn.close()

        if user:
            token = jwt.encode({
                'user_id': user[0],
                'exp': datetime.utcnow() + timedelta(days=7)
            }, app.config['SECRET_KEY'], algorithm="HS256")

            return jsonify({
                'message': '登录成功',
                'token': token,
                'user': {'id': user[0], 'username': user[1]}
            }), 200
        else:
            return jsonify({'message': '用户名或密码错误'}), 401
    except Exception as e:
        print(f'登录错误: {str(e)}')
        return jsonify({'message': '登录失败，请稍后重试'}), 500

# 获取用户信息
@app.route('/api/user/profile', methods=['GET'])
@token_required
def get_profile(current_user_id):
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('SELECT id, username, email, gender, age, height FROM users WHERE id = ?',
              (current_user_id,))
    user = c.fetchone()
    conn.close()

    if user:
        return jsonify({
            'id': user[0],
            'username': user[1],
            'email': user[2],
            'gender': user[3],
            'age': user[4],
            'height': user[5]
        }), 200
    else:
        return jsonify({'message': '用户不存在'}), 404

# 更新用户信息
@app.route('/api/user/profile', methods=['PUT'])
@token_required
def update_profile(current_user_id):
    data = request.json

    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''UPDATE users SET email = ?, gender = ?, age = ?, height = ?
                 WHERE id = ?''',
              (data.get('email'), data.get('gender'), data.get('age'),
               data.get('height'), current_user_id))
    conn.commit()
    conn.close()

    return jsonify({'message': '更新成功'}), 200

# 添加身体数据
@app.route('/api/body-data', methods=['POST'])
@token_required
def add_body_data(current_user_id):
    try:
        data = request.json
        weight = data.get('weight')
        body_fat = data.get('body_fat')
        record_date = data.get('record_date', datetime.now().strftime('%Y-%m-%d'))

        # 参数验证
        if not weight or not body_fat:
            return jsonify({'message': '体重和体脂率不能为空'}), 400

        if weight <= 0 or weight > 500:
            return jsonify({'message': '体重数据不合理'}), 400

        if body_fat < 0 or body_fat > 100:
            return jsonify({'message': '体脂率数据不合理'}), 400

        # 计算BMI (需要身高)
        conn = sqlite3.connect('fitness.db')
        c = conn.cursor()
        c.execute('SELECT height FROM users WHERE id = ?', (current_user_id,))
        user_data = c.fetchone()

        if not user_data:
            conn.close()
            return jsonify({'message': '用户不存在'}), 404

        height = user_data[0]

        bmi = None
        if height and weight:
            bmi = round(weight / ((height / 100) ** 2), 2)

        c.execute('''INSERT INTO body_data (user_id, weight, body_fat, bmi, record_date)
                     VALUES (?, ?, ?, ?, ?)''',
                  (current_user_id, weight, body_fat, bmi, record_date))
        conn.commit()
        conn.close()

        return jsonify({'message': '身体数据添加成功', 'bmi': bmi}), 201
    except Exception as e:
        print(f'添加身体数据错误: {str(e)}')
        return jsonify({'message': '添加失败，请稍后重试'}), 500

# 获取身体数据历史
@app.route('/api/body-data', methods=['GET'])
@token_required
def get_body_data(current_user_id):
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''SELECT id, weight, body_fat, bmi, record_date
                 FROM body_data WHERE user_id = ?
                 ORDER BY record_date DESC LIMIT 30''',
              (current_user_id,))
    records = c.fetchall()
    conn.close()

    data = [{
        'id': r[0],
        'weight': r[1],
        'body_fat': r[2],
        'bmi': r[3],
        'record_date': r[4]
    } for r in records]

    return jsonify(data), 200

# AI生成健身计划
@app.route('/api/fitness-plan/generate', methods=['POST'])
@token_required
def generate_fitness_plan(current_user_id):
    data = request.json
    goal = data.get('goal')  # 减脂、增肌、塑形、保持

    # 获取用户身体数据
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''SELECT u.age, u.gender, u.height, b.weight, b.body_fat
                 FROM users u
                 LEFT JOIN body_data b ON u.id = b.user_id
                 WHERE u.id = ?
                 ORDER BY b.record_date DESC LIMIT 1''',
              (current_user_id,))
    user_data = c.fetchone()

    # AI推荐算法（简化版）
    plan_content = generate_ai_plan(goal, user_data)

    # 保存计划
    c.execute('''INSERT INTO fitness_plans
                 (user_id, plan_name, goal, difficulty, duration, description, plan_content, is_ai_generated)
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?)''',
              (current_user_id, f'AI定制-{goal}计划', goal, plan_content['difficulty'],
               plan_content['duration'], plan_content['description'],
               json.dumps(plan_content['exercises']), True))
    conn.commit()
    plan_id = c.lastrowid
    conn.close()

    return jsonify({
        'message': 'AI计划生成成功',
        'plan_id': plan_id,
        'plan': plan_content
    }), 201

def generate_ai_plan(goal, user_data):
    """AI健身计划生成算法"""
    plans = {
        '减脂': {
            'difficulty': '中等',
            'duration': 8,
            'description': '有氧运动为主，结合力量训练，提高代谢率',
            'exercises': [
                {'name': '慢跑', 'duration': 30, 'calories': 300, 'frequency': '每天'},
                {'name': '开合跳', 'duration': 15, 'calories': 150, 'frequency': '每天'},
                {'name': '波比跳', 'duration': 10, 'calories': 120, 'frequency': '隔天'},
                {'name': '深蹲', 'sets': 3, 'reps': 15, 'frequency': '隔天'},
                {'name': '平板支撑', 'duration': 60, 'frequency': '每天'}
            ]
        },
        '增肌': {
            'difficulty': '困难',
            'duration': 12,
            'description': '力量训练为主，高蛋白饮食配合',
            'exercises': [
                {'name': '卧推', 'sets': 4, 'reps': 8, 'frequency': '每周3次'},
                {'name': '深蹲', 'sets': 4, 'reps': 8, 'frequency': '每周3次'},
                {'name': '硬拉', 'sets': 4, 'reps': 6, 'frequency': '每周2次'},
                {'name': '引体向上', 'sets': 3, 'reps': 10, 'frequency': '每周3次'},
                {'name': '肩推', 'sets': 3, 'reps': 10, 'frequency': '每周2次'}
            ]
        },
        '塑形': {
            'difficulty': '中等',
            'duration': 10,
            'description': '有氧和力量结合，重点雕塑身体线条',
            'exercises': [
                {'name': '瑜伽', 'duration': 45, 'frequency': '每周3次'},
                {'name': '普拉提', 'duration': 30, 'frequency': '每周3次'},
                {'name': '臀桥', 'sets': 3, 'reps': 20, 'frequency': '每天'},
                {'name': '侧平板支撑', 'duration': 30, 'frequency': '每天'},
                {'name': '拉伸训练', 'duration': 20, 'frequency': '每天'}
            ]
        },
        '保持': {
            'difficulty': '简单',
            'duration': 4,
            'description': '轻度运动保持健康状态',
            'exercises': [
                {'name': '快走', 'duration': 30, 'calories': 150, 'frequency': '每天'},
                {'name': '瑜伽', 'duration': 30, 'frequency': '每周3次'},
                {'name': '拉伸', 'duration': 15, 'frequency': '每天'},
                {'name': '深蹲', 'sets': 2, 'reps': 15, 'frequency': '隔天'}
            ]
        }
    }

    return plans.get(goal, plans['保持'])

# 获取健身计划列表
@app.route('/api/fitness-plans', methods=['GET'])
@token_required
def get_fitness_plans(current_user_id):
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''SELECT id, plan_name, goal, difficulty, duration, description, created_at
                 FROM fitness_plans WHERE user_id = ?
                 ORDER BY created_at DESC''',
              (current_user_id,))
    plans = c.fetchall()
    conn.close()

    data = [{
        'id': p[0],
        'plan_name': p[1],
        'goal': p[2],
        'difficulty': p[3],
        'duration': p[4],
        'description': p[5],
        'created_at': p[6]
    } for p in plans]

    return jsonify(data), 200

# 获取计划详情
@app.route('/api/fitness-plans/<int:plan_id>', methods=['GET'])
@token_required
def get_plan_detail(current_user_id, plan_id):
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''SELECT id, plan_name, goal, difficulty, duration, description, plan_content
                 FROM fitness_plans WHERE id = ? AND user_id = ?''',
              (plan_id, current_user_id))
    plan = c.fetchone()
    conn.close()

    if plan:
        return jsonify({
            'id': plan[0],
            'plan_name': plan[1],
            'goal': plan[2],
            'difficulty': plan[3],
            'duration': plan[4],
            'description': plan[5],
            'exercises': json.loads(plan[6]) if plan[6] else []
        }), 200
    else:
        return jsonify({'message': '计划不存在'}), 404

# 添加训练记录
@app.route('/api/training-records', methods=['POST'])
@token_required
def add_training_record(current_user_id):
    data = request.json

    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''INSERT INTO training_records
                 (user_id, plan_id, exercise_name, duration, calories, notes, record_date)
                 VALUES (?, ?, ?, ?, ?, ?, ?)''',
              (current_user_id, data.get('plan_id'), data.get('exercise_name'),
               data.get('duration'), data.get('calories'), data.get('notes'),
               data.get('record_date', datetime.now().strftime('%Y-%m-%d'))))
    conn.commit()
    conn.close()

    return jsonify({'message': '训练记录添加成功'}), 201

# 获取训练记录
@app.route('/api/training-records', methods=['GET'])
@token_required
def get_training_records(current_user_id):
    days = request.args.get('days', 30, type=int)

    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''SELECT id, exercise_name, duration, calories, notes, record_date
                 FROM training_records WHERE user_id = ?
                 ORDER BY record_date DESC LIMIT ?''',
              (current_user_id, days * 10))
    records = c.fetchall()
    conn.close()

    data = [{
        'id': r[0],
        'exercise_name': r[1],
        'duration': r[2],
        'calories': r[3],
        'notes': r[4],
        'record_date': r[5]
    } for r in records]

    return jsonify(data), 200

# 获取训练统计
@app.route('/api/training-stats', methods=['GET'])
@token_required
def get_training_stats(current_user_id):
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()

    # 最近30天的统计
    c.execute('''SELECT record_date, SUM(duration), SUM(calories)
                 FROM training_records
                 WHERE user_id = ? AND record_date >= date('now', '-30 days')
                 GROUP BY record_date
                 ORDER BY record_date''',
              (current_user_id,))
    daily_stats = c.fetchall()

    # 总统计
    c.execute('''SELECT COUNT(*), SUM(duration), SUM(calories)
                 FROM training_records WHERE user_id = ?''',
              (current_user_id,))
    total_stats = c.fetchone()

    conn.close()

    return jsonify({
        'daily_stats': [{
            'date': s[0],
            'duration': s[1],
            'calories': s[2]
        } for s in daily_stats],
        'total': {
            'count': total_stats[0],
            'total_duration': total_stats[1],
            'total_calories': total_stats[2]
        }
    }), 200

# 获取营养建议
@app.route('/api/nutrition/recommendation', methods=['GET'])
@token_required
def get_nutrition_recommendation(current_user_id):
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()

    # 获取用户数据
    c.execute('''SELECT u.age, u.gender, u.height, b.weight, f.goal
                 FROM users u
                 LEFT JOIN body_data b ON u.id = b.user_id
                 LEFT JOIN fitness_plans f ON u.id = f.user_id
                 WHERE u.id = ?
                 ORDER BY b.record_date DESC, f.created_at DESC
                 LIMIT 1''',
              (current_user_id,))
    data = c.fetchone()
    conn.close()

    if not data or not data[3]:
        return jsonify({'message': '请先完善身体数据'}), 400

    age, gender, height, weight, goal = data

    # 计算基础代谢率 (BMR)
    if gender == '男':
        bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * (age or 25))
    else:
        bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * (age or 25))

    # 根据目标调整热量
    calorie_adjustment = {
        '减脂': -500,
        '增肌': 300,
        '塑形': -200,
        '保持': 0
    }

    recommended_calories = bmr * 1.5 + calorie_adjustment.get(goal, 0)

    # 营养素比例
    nutrition_ratio = {
        '减脂': {'protein': 0.35, 'carbs': 0.35, 'fat': 0.30},
        '增肌': {'protein': 0.35, 'carbs': 0.45, 'fat': 0.20},
        '塑形': {'protein': 0.30, 'carbs': 0.40, 'fat': 0.30},
        '保持': {'protein': 0.25, 'carbs': 0.50, 'fat': 0.25}
    }

    ratio = nutrition_ratio.get(goal, nutrition_ratio['保持'])

    return jsonify({
        'daily_calories': round(recommended_calories),
        'protein': round(recommended_calories * ratio['protein'] / 4),
        'carbs': round(recommended_calories * ratio['carbs'] / 4),
        'fat': round(recommended_calories * ratio['fat'] / 9),
        'water': round(weight * 30),
        'meals': [
            '早餐: 燕麦片 + 鸡蛋 + 牛奶',
            '午餐: 糙米饭 + 鸡胸肉 + 蔬菜',
            '晚餐: 全麦面包 + 鱼肉 + 沙拉',
            '加餐: 坚果、水果、酸奶'
        ]
    }), 200

# 社区动态
@app.route('/api/community/posts', methods=['GET'])
@token_required
def get_community_posts(current_user_id):
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''SELECT p.id, p.content, p.image_url, p.likes,
                        datetime(p.created_at, 'localtime') as created_at,
                        u.username, p.user_id
                 FROM community_posts p
                 JOIN users u ON p.user_id = u.id
                 ORDER BY p.created_at DESC LIMIT 50''')
    posts = c.fetchall()

    # 获取当前用户的点赞状态
    post_ids = [p[0] for p in posts]
    if post_ids:
        placeholders = ','.join('?' * len(post_ids))
        c.execute(f'''SELECT post_id FROM post_likes
                     WHERE user_id = ? AND post_id IN ({placeholders})''',
                  [current_user_id] + post_ids)
        liked_posts = set(row[0] for row in c.fetchall())
    else:
        liked_posts = set()

    # 获取每个动态的评论数
    data = []
    for p in posts:
        c.execute('SELECT COUNT(*) FROM post_comments WHERE post_id = ?', (p[0],))
        comment_count = c.fetchone()[0]

        data.append({
            'id': p[0],
            'content': p[1],
            'image_url': p[2],
            'likes': p[3],
            'created_at': p[4],
            'username': p[5],
            'user_id': p[6],
            'is_liked': p[0] in liked_posts,
            'comment_count': comment_count
        })

    conn.close()
    return jsonify(data), 200

# 发布动态
@app.route('/api/community/posts', methods=['POST'])
@token_required
def create_post(current_user_id):
    data = request.json

    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''INSERT INTO community_posts (user_id, content, image_url)
                 VALUES (?, ?, ?)''',
              (current_user_id, data.get('content'), data.get('image_url')))
    conn.commit()
    conn.close()

    return jsonify({'message': '发布成功'}), 201

# 点赞/取消点赞
@app.route('/api/community/posts/<int:post_id>/like', methods=['POST'])
@token_required
def toggle_like(current_user_id, post_id):
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()

    # 检查是否已点赞
    c.execute('SELECT id FROM post_likes WHERE user_id = ? AND post_id = ?',
              (current_user_id, post_id))
    existing_like = c.fetchone()

    if existing_like:
        # 取消点赞
        c.execute('DELETE FROM post_likes WHERE user_id = ? AND post_id = ?',
                  (current_user_id, post_id))
        c.execute('UPDATE community_posts SET likes = likes - 1 WHERE id = ?',
                  (post_id,))
        message = '已取消点赞'
        is_liked = False
    else:
        # 点赞
        c.execute('INSERT INTO post_likes (user_id, post_id) VALUES (?, ?)',
                  (current_user_id, post_id))
        c.execute('UPDATE community_posts SET likes = likes + 1 WHERE id = ?',
                  (post_id,))
        message = '点赞成功'
        is_liked = True

    # 获取最新的点赞数
    c.execute('SELECT likes FROM community_posts WHERE id = ?', (post_id,))
    likes = c.fetchone()[0]

    conn.commit()
    conn.close()

    return jsonify({
        'message': message,
        'is_liked': is_liked,
        'likes': likes
    }), 200

# 添加评论
@app.route('/api/community/posts/<int:post_id>/comments', methods=['POST'])
@token_required
def add_comment(current_user_id, post_id):
    data = request.json
    content = data.get('content')

    if not content or not content.strip():
        return jsonify({'message': '评论内容不能为空'}), 400

    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''INSERT INTO post_comments (user_id, post_id, content)
                 VALUES (?, ?, ?)''',
              (current_user_id, post_id, content))
    conn.commit()
    conn.close()

    return jsonify({'message': '评论成功'}), 201

# 获取评论列表
@app.route('/api/community/posts/<int:post_id>/comments', methods=['GET'])
@token_required
def get_comments(current_user_id, post_id):
    conn = sqlite3.connect('fitness.db')
    c = conn.cursor()
    c.execute('''SELECT c.id, c.content, datetime(c.created_at, 'localtime') as created_at,
                        u.username, c.user_id
                 FROM post_comments c
                 JOIN users u ON c.user_id = u.id
                 WHERE c.post_id = ?
                 ORDER BY c.created_at DESC''',
              (post_id,))
    comments = c.fetchall()
    conn.close()

    data = [{
        'id': c[0],
        'content': c[1],
        'created_at': c[2],
        'username': c[3],
        'user_id': c[4]
    } for c in comments]

    return jsonify(data), 200

if __name__ == '__main__':
    init_db()
    print('数据库初始化完成')
    print('服务器运行在 http://localhost:5000')
    app.run(debug=True, port=5000)
