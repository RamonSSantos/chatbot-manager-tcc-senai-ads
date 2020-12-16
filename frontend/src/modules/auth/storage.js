export const getLocalUser = () => localStorage.getItem('user')
export const deleteLocalUser = () => localStorage.removeItem('user')
export const setLocalUser = (user) => localStorage.setItem('user', user)
