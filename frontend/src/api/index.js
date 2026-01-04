import request from '@/utils/request'

// 用户认证
export const register = (data) => request.post('/register', data)
export const login = (data) => request.post('/login', data)
export const getProfile = () => request.get('/user/profile')
export const updateProfile = (data) => request.put('/user/profile', data)
export const changePassword = (data) => request.post('/user/change-password', data)

// 身体数据
export const addBodyData = (data) => request.post('/body-data', data)
export const getBodyData = () => request.get('/body-data')

// 健身计划
export const generateFitnessPlan = (data) => request.post('/fitness-plans/generate', data)
export const createFitnessPlan = (data) => request.post('/fitness-plans', data)
export const optimizeFitnessPlan = (id, data) => request.put(`/fitness-plans/${id}/optimize`, data)
export const getFitnessPlans = () => request.get('/fitness-plans')
export const getPlanDetail = (id) => request.get(`/fitness-plans/${id}`)

// 训练记录
export const addTrainingRecord = (data) => request.post('/training-records', data)
export const getTrainingRecords = (params) => request.get('/training-records', { params })
export const updateTrainingRecord = (id, data) => request.put(`/training-records/${id}`, data)
export const deleteTrainingRecord = (id) => request.delete(`/training-records/${id}`)
export const getTrainingStats = () => request.get('/training-stats')

// 营养建议
export const getNutritionRecommendation = () => request.get('/nutrition/recommendation')

// 社区
export const getCommunityPosts = () => request.get('/community/posts')
export const createPost = (data) => request.post('/community/posts', data)
export const toggleLike = (postId) => request.post(`/community/posts/${postId}/like`)
export const addComment = (postId, data) => request.post(`/community/posts/${postId}/comments`, data)
export const getComments = (postId) => request.get(`/community/posts/${postId}/comments`)
