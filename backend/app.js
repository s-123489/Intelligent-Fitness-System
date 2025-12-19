const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const crypto = require('crypto');
const sqlite3 = require('sqlite3').verbose();

const app = express();
const db = new sqlite3.Database('fitness.db');

// 中间件
app.use(cors());
app.use(express.json());

const SECRET_KEY = 'your-secret-key-here';

// 数据库初始化
function initDb() {
    db.serialize(() => {
        // 用户表
        db.run(`CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            email TEXT,
            gender TEXT,
            age INTEGER,
            height REAL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )`);

        // 身体数据表
        db.run(`CREATE TABLE IF NOT EXISTS body_data (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            weight REAL,
            body_fat REAL,
            bmi REAL,
            record_date DATE,
            FOREIGN KEY (user_id) REFERENCES users(id)
        )`);

        // 健身计划表
        db.run(`CREATE TABLE IF NOT EXISTS fitness_plans (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            plan_name TEXT,
            goal TEXT,
            difficulty TEXT,
            duration INTEGER,
            description TEXT,
            plan_content TEXT,
            is_ai_generated BOOLEAN,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id)
        )`);

        // 训练记录表
        db.run(`CREATE TABLE IF NOT EXISTS training_records (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            plan_id INTEGER,
            exercise_name TEXT,
            duration INTEGER,
            calories REAL,
            notes TEXT,
            record_date DATE,
            FOREIGN KEY (user_id) REFERENCES users(id),
            FOREIGN KEY (plan_id) REFERENCES fitness_plans(id)
        )`);

        // 营养记录表
        db.run(`CREATE TABLE IF NOT EXISTS nutrition_records (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            meal_type TEXT,
            food_name TEXT,
            calories REAL,
            protein REAL,
            carbs REAL,
            fat REAL,
            record_date DATE,
            FOREIGN KEY (user_id) REFERENCES users(id)
        )`);

        // 社区动态表
        db.run(`CREATE TABLE IF NOT EXISTS community_posts (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            content TEXT,
            image_url TEXT,
            likes INTEGER DEFAULT 0,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id)
        )`);

        // 点赞记录表
        db.run(`CREATE TABLE IF NOT EXISTS post_likes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            post_id INTEGER,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            UNIQUE(user_id, post_id),
            FOREIGN KEY (user_id) REFERENCES users(id),
            FOREIGN KEY (post_id) REFERENCES community_posts(id)
        )`);

        // 评论表
        db.run(`CREATE TABLE IF NOT EXISTS post_comments (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            post_id INTEGER,
            content TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id),
            FOREIGN KEY (post_id) REFERENCES community_posts(id)
        )`);

        console.log('数据库初始化完成');
    });
}

// JWT认证中间件
function tokenRequired(req, res, next) {
    const authHeader = req.headers['authorization'];

    if (!authHeader) {
        return res.status(401).json({ message: '缺少认证令牌' });
    }

    let token = authHeader;
    if (authHeader.startsWith('Bearer ')) {
        token = authHeader.substring(7);
    }

    try {
        const decoded = jwt.verify(token, SECRET_KEY);
        req.userId = decoded.user_id;
        next();
    } catch (error) {
        return res.status(401).json({ message: '无效的认证令牌' });
    }
}

// 密码加密
function hashPassword(password) {
    return crypto.createHash('sha256').update(password).digest('hex');
}

// 用户注册
app.post('/api/register', (req, res) => {
    const { username, password, email } = req.body;

    if (!username || !password) {
        return res.status(400).json({ message: '用户名和密码不能为空' });
    }

    const hashedPassword = hashPassword(password);

    db.run('INSERT INTO users (username, password, email) VALUES (?, ?, ?)',
        [username, hashedPassword, email],
        function(err) {
            if (err) {
                if (err.message.includes('UNIQUE')) {
                    return res.status(400).json({ message: '用户名已存在' });
                }
                return res.status(500).json({ message: '注册失败' });
            }
            res.status(201).json({
                message: '注册成功',
                user_id: this.lastID
            });
        }
    );
});

// 用户登录
app.post('/api/login', (req, res) => {
    const { username, password } = req.body;
    const hashedPassword = hashPassword(password);

    db.get('SELECT id, username FROM users WHERE username = ? AND password = ?',
        [username, hashedPassword],
        (err, user) => {
            if (err) {
                return res.status(500).json({ message: '登录失败' });
            }

            if (user) {
                const token = jwt.sign(
                    { user_id: user.id },
                    SECRET_KEY,
                    { expiresIn: '7d' }
                );

                res.json({
                    message: '登录成功',
                    token: token,
                    user: { id: user.id, username: user.username }
                });
            } else {
                res.status(401).json({ message: '用户名或密码错误' });
            }
        }
    );
});

// 获取用户信息
app.get('/api/user/profile', tokenRequired, (req, res) => {
    db.get('SELECT id, username, email, gender, age, height FROM users WHERE id = ?',
        [req.userId],
        (err, user) => {
            if (err) {
                return res.status(500).json({ message: '查询失败' });
            }
            if (user) {
                res.json(user);
            } else {
                res.status(404).json({ message: '用户不存在' });
            }
        }
    );
});

// 更新用户信息
app.put('/api/user/profile', tokenRequired, (req, res) => {
    const { email, gender, age, height } = req.body;

    db.run('UPDATE users SET email = ?, gender = ?, age = ?, height = ? WHERE id = ?',
        [email, gender, age, height, req.userId],
        (err) => {
            if (err) {
                return res.status(500).json({ message: '更新失败' });
            }
            res.json({ message: '更新成功' });
        }
    );
});

// 添加身体数据
app.post('/api/body-data', tokenRequired, (req, res) => {
    const { weight, body_fat, record_date } = req.body;
    const date = record_date || new Date().toISOString().split('T')[0];

    // 获取用户身高计算BMI
    db.get('SELECT height FROM users WHERE id = ?', [req.userId], (err, user) => {
        if (err) {
            return res.status(500).json({ message: '查询失败' });
        }

        let bmi = null;
        if (user && user.height && weight) {
            bmi = Math.round((weight / Math.pow(user.height / 100, 2)) * 100) / 100;
        }

        db.run('INSERT INTO body_data (user_id, weight, body_fat, bmi, record_date) VALUES (?, ?, ?, ?, ?)',
            [req.userId, weight, body_fat, bmi, date],
            function(err) {
                if (err) {
                    return res.status(500).json({ message: '添加失败' });
                }
                res.status(201).json({ message: '身体数据添加成功', bmi });
            }
        );
    });
});

// 获取身体数据历史
app.get('/api/body-data', tokenRequired, (req, res) => {
    db.all(`SELECT id, weight, body_fat, bmi, record_date
            FROM body_data
            WHERE user_id = ?
            ORDER BY record_date DESC
            LIMIT 30`,
        [req.userId],
        (err, records) => {
            if (err) {
                return res.status(500).json({ message: '查询失败' });
            }
            res.json(records);
        }
    );
});

// AI生成健身计划
function generateAiPlan(goal, userData) {
    const plans = {
        '减脂': {
            difficulty: '中等',
            duration: 8,
            description: '有氧运动为主，结合力量训练，提高代谢率',
            exercises: [
                { name: '慢跑', duration: 30, calories: 300, frequency: '每天' },
                { name: '开合跳', duration: 15, calories: 150, frequency: '每天' },
                { name: '波比跳', duration: 10, calories: 120, frequency: '隔天' },
                { name: '深蹲', sets: 3, reps: 15, frequency: '隔天' },
                { name: '平板支撑', duration: 60, frequency: '每天' }
            ]
        },
        '增肌': {
            difficulty: '困难',
            duration: 12,
            description: '力量训练为主，高蛋白饮食配合',
            exercises: [
                { name: '卧推', sets: 4, reps: 8, frequency: '每周3次' },
                { name: '深蹲', sets: 4, reps: 8, frequency: '每周3次' },
                { name: '硬拉', sets: 4, reps: 6, frequency: '每周2次' },
                { name: '引体向上', sets: 3, reps: 10, frequency: '每周3次' },
                { name: '肩推', sets: 3, reps: 10, frequency: '每周2次' }
            ]
        },
        '塑形': {
            difficulty: '中等',
            duration: 10,
            description: '有氧和力量结合，重点雕塑身体线条',
            exercises: [
                { name: '瑜伽', duration: 45, frequency: '每周3次' },
                { name: '普拉提', duration: 30, frequency: '每周3次' },
                { name: '臀桥', sets: 3, reps: 20, frequency: '每天' },
                { name: '侧平板支撑', duration: 30, frequency: '每天' },
                { name: '拉伸训练', duration: 20, frequency: '每天' }
            ]
        },
        '保持': {
            difficulty: '简单',
            duration: 4,
            description: '轻度运动保持健康状态',
            exercises: [
                { name: '快走', duration: 30, calories: 150, frequency: '每天' },
                { name: '瑜伽', duration: 30, frequency: '每周3次' },
                { name: '拉伸', duration: 15, frequency: '每天' },
                { name: '深蹲', sets: 2, reps: 15, frequency: '隔天' }
            ]
        }
    };

    return plans[goal] || plans['保持'];
}

app.post('/api/fitness-plan/generate', tokenRequired, (req, res) => {
    const { goal } = req.body;

    // 获取用户数据
    db.get(`SELECT u.age, u.gender, u.height, b.weight, b.body_fat
            FROM users u
            LEFT JOIN body_data b ON u.id = b.user_id
            WHERE u.id = ?
            ORDER BY b.record_date DESC
            LIMIT 1`,
        [req.userId],
        (err, userData) => {
            if (err) {
                return res.status(500).json({ message: '查询失败' });
            }

            // 生成AI计划
            const planContent = generateAiPlan(goal, userData);

            // 保存计划
            db.run(`INSERT INTO fitness_plans
                    (user_id, plan_name, goal, difficulty, duration, description, plan_content, is_ai_generated)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
                [req.userId, `AI定制-${goal}计划`, goal, planContent.difficulty,
                 planContent.duration, planContent.description,
                 JSON.stringify(planContent.exercises), 1],
                function(err) {
                    if (err) {
                        return res.status(500).json({ message: '生成失败' });
                    }
                    res.status(201).json({
                        message: 'AI计划生成成功',
                        plan_id: this.lastID,
                        plan: planContent
                    });
                }
            );
        }
    );
});

// 获取健身计划列表
app.get('/api/fitness-plans', tokenRequired, (req, res) => {
    db.all(`SELECT id, plan_name, goal, difficulty, duration, description, created_at
            FROM fitness_plans
            WHERE user_id = ?
            ORDER BY created_at DESC`,
        [req.userId],
        (err, plans) => {
            if (err) {
                return res.status(500).json({ message: '查询失败' });
            }
            res.json(plans);
        }
    );
});

// 获取计划详情
app.get('/api/fitness-plans/:plan_id', tokenRequired, (req, res) => {
    db.get(`SELECT id, plan_name, goal, difficulty, duration, description, plan_content
            FROM fitness_plans
            WHERE id = ? AND user_id = ?`,
        [req.params.plan_id, req.userId],
        (err, plan) => {
            if (err) {
                return res.status(500).json({ message: '查询失败' });
            }

            if (plan) {
                res.json({
                    ...plan,
                    exercises: plan.plan_content ? JSON.parse(plan.plan_content) : []
                });
            } else {
                res.status(404).json({ message: '计划不存在' });
            }
        }
    );
});

// 添加训练记录
app.post('/api/training-records', tokenRequired, (req, res) => {
    const { plan_id, exercise_name, duration, calories, notes, record_date } = req.body;
    const date = record_date || new Date().toISOString().split('T')[0];

    db.run(`INSERT INTO training_records
            (user_id, plan_id, exercise_name, duration, calories, notes, record_date)
            VALUES (?, ?, ?, ?, ?, ?, ?)`,
        [req.userId, plan_id, exercise_name, duration, calories, notes, date],
        function(err) {
            if (err) {
                return res.status(500).json({ message: '添加失败' });
            }
            res.status(201).json({ message: '训练记录添加成功' });
        }
    );
});

// 获取训练记录
app.get('/api/training-records', tokenRequired, (req, res) => {
    const days = parseInt(req.query.days) || 30;

    db.all(`SELECT id, exercise_name, duration, calories, notes, record_date
            FROM training_records
            WHERE user_id = ?
            ORDER BY record_date DESC
            LIMIT ?`,
        [req.userId, days * 10],
        (err, records) => {
            if (err) {
                return res.status(500).json({ message: '查询失败' });
            }
            res.json(records);
        }
    );
});

// 获取训练统计
app.get('/api/training-stats', tokenRequired, (req, res) => {
    // 最近30天的统计
    db.all(`SELECT record_date, SUM(duration) as duration, SUM(calories) as calories
            FROM training_records
            WHERE user_id = ? AND record_date >= date('now', '-30 days')
            GROUP BY record_date
            ORDER BY record_date`,
        [req.userId],
        (err, dailyStats) => {
            if (err) {
                return res.status(500).json({ message: '查询失败' });
            }

            // 总统计
            db.get(`SELECT COUNT(*) as count, SUM(duration) as total_duration, SUM(calories) as total_calories
                    FROM training_records
                    WHERE user_id = ?`,
                [req.userId],
                (err, totalStats) => {
                    if (err) {
                        return res.status(500).json({ message: '查询失败' });
                    }

                    res.json({
                        daily_stats: dailyStats.map(s => ({
                            date: s.record_date,
                            duration: s.duration,
                            calories: s.calories
                        })),
                        total: {
                            count: totalStats.count || 0,
                            total_duration: totalStats.total_duration || 0,
                            total_calories: totalStats.total_calories || 0
                        }
                    });
                }
            );
        }
    );
});

// 获取营养建议
app.get('/api/nutrition/recommendation', tokenRequired, (req, res) => {
    db.get(`SELECT u.age, u.gender, u.height, b.weight, f.goal
            FROM users u
            LEFT JOIN body_data b ON u.id = b.user_id
            LEFT JOIN fitness_plans f ON u.id = f.user_id
            WHERE u.id = ?
            ORDER BY b.record_date DESC, f.created_at DESC
            LIMIT 1`,
        [req.userId],
        (err, data) => {
            if (err) {
                return res.status(500).json({ message: '查询失败' });
            }

            if (!data || !data.weight) {
                return res.status(400).json({ message: '请先完善身体数据' });
            }

            const { age, gender, height, weight, goal } = data;

            // 计算基础代谢率 (BMR)
            let bmr;
            if (gender === '男') {
                bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * (age || 25));
            } else {
                bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * (age || 25));
            }

            // 根据目标调整热量
            const calorieAdjustment = {
                '减脂': -500,
                '增肌': 300,
                '塑形': -200,
                '保持': 0
            };

            const recommendedCalories = bmr * 1.5 + (calorieAdjustment[goal] || 0);

            // 营养素比例
            const nutritionRatio = {
                '减脂': { protein: 0.35, carbs: 0.35, fat: 0.30 },
                '增肌': { protein: 0.35, carbs: 0.45, fat: 0.20 },
                '塑形': { protein: 0.30, carbs: 0.40, fat: 0.30 },
                '保持': { protein: 0.25, carbs: 0.50, fat: 0.25 }
            };

            const ratio = nutritionRatio[goal] || nutritionRatio['保持'];

            res.json({
                daily_calories: Math.round(recommendedCalories),
                protein: Math.round(recommendedCalories * ratio.protein / 4),
                carbs: Math.round(recommendedCalories * ratio.carbs / 4),
                fat: Math.round(recommendedCalories * ratio.fat / 9),
                water: Math.round(weight * 30),
                meals: [
                    '早餐: 燕麦片 + 鸡蛋 + 牛奶',
                    '午餐: 糙米饭 + 鸡胸肉 + 蔬菜',
                    '晚餐: 全麦面包 + 鱼肉 + 沙拉',
                    '加餐: 坚果、水果、酸奶'
                ]
            });
        }
    );
});

// 获取社区动态
app.get('/api/community/posts', tokenRequired, (req, res) => {
    db.all(`SELECT p.id, p.content, p.image_url, p.likes,
                   datetime(p.created_at, 'localtime') as created_at,
                   u.username, p.user_id
            FROM community_posts p
            JOIN users u ON p.user_id = u.id
            ORDER BY p.created_at DESC
            LIMIT 50`,
        [],
        (err, posts) => {
            if (err) {
                return res.status(500).json({ message: '查询失败' });
            }

            // 获取当前用户的点赞状态
            const postIds = posts.map(p => p.id);

            if (postIds.length === 0) {
                return res.json([]);
            }

            const placeholders = postIds.map(() => '?').join(',');
            db.all(`SELECT post_id
                    FROM post_likes
                    WHERE user_id = ? AND post_id IN (${placeholders})`,
                [req.userId, ...postIds],
                (err, likes) => {
                    if (err) {
                        return res.status(500).json({ message: '查询失败' });
                    }

                    const likedPosts = new Set(likes.map(l => l.post_id));

                    // 获取每个动态的评论数
                    let completed = 0;
                    const data = [];

                    posts.forEach((p, index) => {
                        db.get('SELECT COUNT(*) as count FROM post_comments WHERE post_id = ?',
                            [p.id],
                            (err, result) => {
                                if (err) {
                                    return res.status(500).json({ message: '查询失败' });
                                }

                                data[index] = {
                                    id: p.id,
                                    content: p.content,
                                    image_url: p.image_url,
                                    likes: p.likes,
                                    created_at: p.created_at,
                                    username: p.username,
                                    user_id: p.user_id,
                                    is_liked: likedPosts.has(p.id),
                                    comment_count: result.count
                                };

                                completed++;
                                if (completed === posts.length) {
                                    res.json(data);
                                }
                            }
                        );
                    });
                }
            );
        }
    );
});

// 发布动态
app.post('/api/community/posts', tokenRequired, (req, res) => {
    const { content, image_url } = req.body;

    db.run('INSERT INTO community_posts (user_id, content, image_url) VALUES (?, ?, ?)',
        [req.userId, content, image_url],
        function(err) {
            if (err) {
                return res.status(500).json({ message: '发布失败' });
            }
            res.status(201).json({ message: '发布成功' });
        }
    );
});

// 点赞/取消点赞
app.post('/api/community/posts/:post_id/like', tokenRequired, (req, res) => {
    const postId = req.params.post_id;

    // 检查是否已点赞
    db.get('SELECT id FROM post_likes WHERE user_id = ? AND post_id = ?',
        [req.userId, postId],
        (err, existingLike) => {
            if (err) {
                return res.status(500).json({ message: '操作失败' });
            }

            let message, isLiked;

            if (existingLike) {
                // 取消点赞
                db.run('DELETE FROM post_likes WHERE user_id = ? AND post_id = ?',
                    [req.userId, postId],
                    (err) => {
                        if (err) {
                            return res.status(500).json({ message: '操作失败' });
                        }

                        db.run('UPDATE community_posts SET likes = likes - 1 WHERE id = ?',
                            [postId],
                            (err) => {
                                if (err) {
                                    return res.status(500).json({ message: '操作失败' });
                                }

                                db.get('SELECT likes FROM community_posts WHERE id = ?',
                                    [postId],
                                    (err, result) => {
                                        if (err) {
                                            return res.status(500).json({ message: '操作失败' });
                                        }
                                        res.json({
                                            message: '已取消点赞',
                                            is_liked: false,
                                            likes: result.likes
                                        });
                                    }
                                );
                            }
                        );
                    }
                );
            } else {
                // 点赞
                db.run('INSERT INTO post_likes (user_id, post_id) VALUES (?, ?)',
                    [req.userId, postId],
                    (err) => {
                        if (err) {
                            return res.status(500).json({ message: '操作失败' });
                        }

                        db.run('UPDATE community_posts SET likes = likes + 1 WHERE id = ?',
                            [postId],
                            (err) => {
                                if (err) {
                                    return res.status(500).json({ message: '操作失败' });
                                }

                                db.get('SELECT likes FROM community_posts WHERE id = ?',
                                    [postId],
                                    (err, result) => {
                                        if (err) {
                                            return res.status(500).json({ message: '操作失败' });
                                        }
                                        res.json({
                                            message: '点赞成功',
                                            is_liked: true,
                                            likes: result.likes
                                        });
                                    }
                                );
                            }
                        );
                    }
                );
            }
        }
    );
});

// 添加评论
app.post('/api/community/posts/:post_id/comments', tokenRequired, (req, res) => {
    const postId = req.params.post_id;
    const { content } = req.body;

    if (!content || !content.trim()) {
        return res.status(400).json({ message: '评论内容不能为空' });
    }

    db.run('INSERT INTO post_comments (user_id, post_id, content) VALUES (?, ?, ?)',
        [req.userId, postId, content],
        function(err) {
            if (err) {
                return res.status(500).json({ message: '评论失败' });
            }
            res.status(201).json({ message: '评论成功' });
        }
    );
});

// 获取评论列表
app.get('/api/community/posts/:post_id/comments', tokenRequired, (req, res) => {
    const postId = req.params.post_id;

    db.all(`SELECT c.id, c.content, datetime(c.created_at, 'localtime') as created_at,
                   u.username, c.user_id
            FROM post_comments c
            JOIN users u ON c.user_id = u.id
            WHERE c.post_id = ?
            ORDER BY c.created_at DESC`,
        [postId],
        (err, comments) => {
            if (err) {
                return res.status(500).json({ message: '查询失败' });
            }
            res.json(comments);
        }
    );
});

// 启动服务器
const PORT = 5000;
initDb();
app.listen(PORT, () => {
    console.log(`服务器运行在 http://localhost:${PORT}`);
});
