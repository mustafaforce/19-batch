CREATE TABLE IF NOT EXISTS public.teacher_availability (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    is_active BOOLEAN DEFAULT false,
    time_from TEXT DEFAULT '09:00 AM',
    time_to TEXT DEFAULT '05:00 PM',
    updated_at TIMESTAMPTZ DEFAULT now()
);

ALTER TABLE public.teacher_availability ENABLE ROW LEVEL SECURITY;

-- Teachers can insert their own availability
DROP POLICY IF EXISTS "Teachers can insert own availability" ON public.teacher_availability;
CREATE POLICY "Teachers can insert own availability"
ON public.teacher_availability FOR INSERT
WITH CHECK (auth.uid() = teacher_id);

-- Teachers can update their own availability
DROP POLICY IF EXISTS "Teachers can update own availability" ON public.teacher_availability;
CREATE POLICY "Teachers can update own availability"
ON public.teacher_availability FOR UPDATE
USING (auth.uid() = teacher_id)
WITH CHECK (auth.uid() = teacher_id);

-- Everyone authenticated can read availability
DROP POLICY IF EXISTS "Anyone can read availability" ON public.teacher_availability;
CREATE POLICY "Anyone can read availability"
ON public.teacher_availability FOR SELECT
USING (true);

-- Students can read teacher profiles (needed for teacher list)
DROP POLICY IF EXISTS "Anyone can read teacher profiles" ON public.profiles;
CREATE POLICY "Anyone can read teacher profiles"
ON public.profiles FOR SELECT
USING (true);
