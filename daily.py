from __future__ import annotations

from dataclasses import dataclass, field
from datetime import date, datetime
from typing import Callable, Dict, Iterable, List


@dataclass(frozen=True)
class Routine:
    """Routine configuration used for creating daily sessions."""

    id: int
    title: str
    checks_per_day: int
    is_active: bool = True


@dataclass
class DailyCheck:
    """State of a single checkbox inside a session."""

    session_id: str
    check_index: int
    is_completed: bool


@dataclass
class DailySession:
    """Per-day session for a specific routine."""

    id: str
    routine_id: int
    day: date
    checks: Dict[int, DailyCheck] = field(default_factory=dict)


@dataclass
class DailySummary:
    """Daily aggregate information for Today screen."""

    day: date
    percent: float
    computed_at: datetime
    computed_by: str
    total_items: int
    done_items: int


class DailySessionService:
    """Business logic for today screen and progress calculations."""

    @staticmethod
    def open_today(
        *,
        day: date,
        active_routines: Iterable[Routine],
        sessions: Dict[str, DailySession],
    ) -> List[DailySession]:
        """Ensures one session exists for each active routine on a given day."""
        result: List[DailySession] = []
        for routine in active_routines:
            if not routine.is_active:
                continue

            session_id = DailySessionService._session_id(day=day, routine_id=routine.id)
            session = sessions.get(session_id)
            if session is None:
                session = DailySession(id=session_id, routine_id=routine.id, day=day)
                sessions[session_id] = session
            result.append(session)

        return result

    @staticmethod
    def toggle_check(
        *,
        session: DailySession,
        check_index: int,
        is_completed: bool,
    ) -> DailyCheck:
        """Persists checkbox state in DailyCheck and returns saved object."""
        check = DailyCheck(
            session_id=session.id,
            check_index=check_index,
            is_completed=is_completed,
        )
        session.checks[check_index] = check
        return check

    @staticmethod
    def routine_completion_percent(*, session: DailySession, checks_per_day: int) -> float:
        """Completion percent for one routine in the given session."""
        if checks_per_day <= 0:
            return 0.0

        completed_count = sum(1 for check in session.checks.values() if check.is_completed)
        return round((completed_count / checks_per_day) * 100, 2)

    @staticmethod
    def overall_completion_percent(
        *,
        sessions: Iterable[DailySession],
        checks_per_day_by_routine: Dict[int, int],
    ) -> float:
        """Weighted completion percent across all routines for a day."""
        total_completed, total_checks = DailySessionService._totals(
            sessions=sessions,
            checks_per_day_by_routine=checks_per_day_by_routine,
        )

        if total_checks == 0:
            return 0.0

        return round((total_completed / total_checks) * 100, 2)

    @staticmethod
    def recompute_today_summary(
        *,
        day: date,
        routines: Iterable[Routine],
        sessions: Dict[str, DailySession],
        summaries: Dict[date, DailySummary],
        computed_by: str,
        now_provider: Callable[[], datetime] = datetime.utcnow,
    ) -> DailySummary:
        """Recomputes and upserts DailySummary for the date ("Подвести итог" action)."""
        active_routines = [routine for routine in routines if routine.is_active]
        day_sessions = DailySessionService.open_today(
            day=day,
            active_routines=active_routines,
            sessions=sessions,
        )
        checks_per_day_by_routine = {
            routine.id: routine.checks_per_day for routine in active_routines
        }

        done_items, total_items = DailySessionService._totals(
            sessions=day_sessions,
            checks_per_day_by_routine=checks_per_day_by_routine,
        )
        percent = round((done_items / total_items) * 100, 2) if total_items > 0 else 0.0

        summary = DailySummary(
            day=day,
            percent=percent,
            computed_at=now_provider(),
            computed_by=computed_by,
            total_items=total_items,
            done_items=done_items,
        )
        summaries[day] = summary
        return summary

    @staticmethod
    def _totals(
        *,
        sessions: Iterable[DailySession],
        checks_per_day_by_routine: Dict[int, int],
    ) -> tuple[int, int]:
        total_checks = 0
        total_completed = 0

        for session in sessions:
            checks_for_routine = checks_per_day_by_routine.get(session.routine_id, 0)
            if checks_for_routine <= 0:
                continue

            total_checks += checks_for_routine
            completed_in_session = sum(
                1 for check in session.checks.values() if check.is_completed
            )
            total_completed += min(completed_in_session, checks_for_routine)

        return total_completed, total_checks

    @staticmethod
    def _session_id(*, day: date, routine_id: int) -> str:
        return f"{day.isoformat()}:{routine_id}"
