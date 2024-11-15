CREATE TABLE IF NOT EXISTS public.employee (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    nickname VARCHAR(255),
    userid VARCHAR(255) NOT NULL,
    gender VARCHAR(50),
    contact_phone VARCHAR(50),
    sub_account BOOLEAN,
    image_path VARCHAR(255),
    image_url VARCHAR(255),
    relate_account_uid VARCHAR(255)
);

-- 创建员工图片表
CREATE TABLE IF NOT EXISTS public.employee_image (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    image_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES public.employee(id)
);

CREATE INDEX IF NOT EXISTS idx_favorite_user_name ON public.favorite(user_name);
CREATE INDEX IF NOT EXISTS idx_favorite_target_id ON public.favorite(target_id);



