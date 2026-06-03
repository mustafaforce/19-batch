-- Run in Supabase SQL Editor to create the chat-images bucket with policies

INSERT INTO storage.buckets (id, name, public)
VALUES ('chat-images', 'chat-images', true)
ON CONFLICT (id) DO NOTHING;

-- Allow authenticated users to upload
DROP POLICY IF EXISTS "Users can upload chat images" ON storage.objects;
CREATE POLICY "Users can upload chat images"
ON storage.objects FOR INSERT
WITH CHECK (
    bucket_id = 'chat-images'
    AND auth.role() = 'authenticated'
);

-- Allow public read access
DROP POLICY IF EXISTS "Anyone can read chat images" ON storage.objects;
CREATE POLICY "Anyone can read chat images"
ON storage.objects FOR SELECT
USING (bucket_id = 'chat-images');
