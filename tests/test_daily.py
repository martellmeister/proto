import unittest
from datetime import date, datetime

from daily import DailySession, DailySessionService, Routine


class DailySessionServiceTests(unittest.TestCase):
    def test_routine_completion_percent_partial(self) -> None:
        session = DailySession(id="2026-01-01:1", routine_id=1, day=date(2026, 1, 1))
        DailySessionService.toggle_check(session=session, check_index=0, is_completed=True)
        DailySessionService.toggle_check(session=session, check_index=1, is_completed=True)
        DailySessionService.toggle_check(session=session, check_index=2, is_completed=False)

        percent = DailySessionService.routine_completion_percent(session=session, checks_per_day=4)

        self.assertEqual(percent, 50.0)

    def test_routine_completion_percent_without_checks(self) -> None:
        session = DailySession(id="2026-01-01:1", routine_id=1, day=date(2026, 1, 1))

        percent = DailySessionService.routine_completion_percent(session=session, checks_per_day=0)

        self.assertEqual(percent, 0.0)

    def test_overall_completion_percent_weighted(self) -> None:
        session_1 = DailySession(id="2026-01-01:1", routine_id=1, day=date(2026, 1, 1))
        session_2 = DailySession(id="2026-01-01:2", routine_id=2, day=date(2026, 1, 1))

        DailySessionService.toggle_check(session=session_1, check_index=0, is_completed=True)
        DailySessionService.toggle_check(session=session_1, check_index=1, is_completed=False)

        DailySessionService.toggle_check(session=session_2, check_index=0, is_completed=True)
        DailySessionService.toggle_check(session=session_2, check_index=1, is_completed=True)
        DailySessionService.toggle_check(session=session_2, check_index=2, is_completed=False)

        percent = DailySessionService.overall_completion_percent(
            sessions=[session_1, session_2],
            checks_per_day_by_routine={1: 2, 2: 3},
        )

        self.assertEqual(percent, 60.0)

    def test_open_today_creates_missing_sessions_for_active_routines(self) -> None:
        day = date(2026, 1, 1)
        routines = [
            Routine(id=1, title="Water", checks_per_day=1, is_active=True),
            Routine(id=2, title="Workout", checks_per_day=1, is_active=False),
            Routine(id=3, title="Read", checks_per_day=1, is_active=True),
        ]
        sessions = {}

        opened = DailySessionService.open_today(day=day, active_routines=routines, sessions=sessions)

        self.assertEqual(len(opened), 2)
        self.assertIn("2026-01-01:1", sessions)
        self.assertIn("2026-01-01:3", sessions)
        self.assertNotIn("2026-01-01:2", sessions)

    def test_recompute_today_summary_upserts_for_date(self) -> None:
        day = date(2026, 1, 1)
        routines = [
            Routine(id=1, title="Water", checks_per_day=2, is_active=True),
            Routine(id=2, title="Workout", checks_per_day=1, is_active=True),
        ]
        sessions = {}
        summaries = {}

        day_sessions = DailySessionService.open_today(
            day=day,
            active_routines=routines,
            sessions=sessions,
        )
        DailySessionService.toggle_check(session=day_sessions[0], check_index=0, is_completed=True)
        DailySessionService.toggle_check(session=day_sessions[1], check_index=0, is_completed=True)

        summary = DailySessionService.recompute_today_summary(
            day=day,
            routines=routines,
            sessions=sessions,
            summaries=summaries,
            computed_by="today.button",
            now_provider=lambda: datetime(2026, 1, 1, 12, 0, 0),
        )

        self.assertEqual(summary.percent, 66.67)
        self.assertEqual(summary.total_items, 3)
        self.assertEqual(summary.done_items, 2)
        self.assertEqual(summary.computed_by, "today.button")
        self.assertEqual(summaries[day], summary)

    def test_recompute_today_summary_recalculates_on_repeat_click(self) -> None:
        day = date(2026, 1, 1)
        routines = [Routine(id=1, title="Read", checks_per_day=2, is_active=True)]
        sessions = {}
        summaries = {}

        day_sessions = DailySessionService.open_today(
            day=day,
            active_routines=routines,
            sessions=sessions,
        )
        DailySessionService.toggle_check(session=day_sessions[0], check_index=0, is_completed=True)

        first = DailySessionService.recompute_today_summary(
            day=day,
            routines=routines,
            sessions=sessions,
            summaries=summaries,
            computed_by="today.button",
            now_provider=lambda: datetime(2026, 1, 1, 8, 0, 0),
        )

        DailySessionService.toggle_check(session=day_sessions[0], check_index=1, is_completed=True)

        second = DailySessionService.recompute_today_summary(
            day=day,
            routines=routines,
            sessions=sessions,
            summaries=summaries,
            computed_by="today.button",
            now_provider=lambda: datetime(2026, 1, 1, 9, 0, 0),
        )

        self.assertEqual(first.percent, 50.0)
        self.assertEqual(second.percent, 100.0)
        self.assertEqual(second.done_items, 2)
        self.assertEqual(second.computed_at, datetime(2026, 1, 1, 9, 0, 0))
        self.assertEqual(summaries[day], second)

    def test_recompute_today_summary_counts_only_active_routines(self) -> None:
        day = date(2026, 1, 1)
        active = Routine(id=1, title="Read", checks_per_day=2, is_active=True)
        inactive = Routine(id=2, title="Workout", checks_per_day=5, is_active=False)
        sessions = {
            "2026-01-01:2": DailySession(id="2026-01-01:2", routine_id=2, day=day),
        }
        summaries = {}

        active_session = DailySessionService.open_today(
            day=day,
            active_routines=[active],
            sessions=sessions,
        )[0]
        DailySessionService.toggle_check(session=active_session, check_index=0, is_completed=True)

        DailySessionService.toggle_check(
            session=sessions["2026-01-01:2"],
            check_index=0,
            is_completed=True,
        )

        summary = DailySessionService.recompute_today_summary(
            day=day,
            routines=[active, inactive],
            sessions=sessions,
            summaries=summaries,
            computed_by="today.button",
            now_provider=lambda: datetime(2026, 1, 1, 10, 0, 0),
        )

        self.assertEqual(summary.total_items, 2)
        self.assertEqual(summary.done_items, 1)
        self.assertEqual(summary.percent, 50.0)


if __name__ == "__main__":
    unittest.main()
