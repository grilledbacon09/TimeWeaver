package com.example.timeweaver.screens

import android.util.Log
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.timeweaver.navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.navigation.NavViewModel
import com.example.timeweaver.roomDB.TodoDatabase
import com.example.timeweaver.roomDB.TodoEntity
import com.example.timeweaver.roomDB.TodoRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.timeweaver.roomDB.FixedDatabase
import com.example.timeweaver.roomDB.FixedEntity
import com.example.timeweaver.roomDB.FixedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun MyScreen(navController: NavHostController) {
    val context = LocalContext.current
    val database = TodoDatabase.getItemDatabase(context)
    val FixedDB = FixedDatabase.getFixedDatabase(context)
    val todoRepository = TodoRepository(database)
    val fixedRepository = FixedRepository(FixedDB)
    val navViewModel: NavViewModel = viewModel(LocalNavGraphViewModelStoreOwner.current)

    val todoEntities: LiveData<List<TodoEntity>> = todoRepository.getAll()
    val todoEntitiesState: List<TodoEntity> by todoEntities.observeAsState(emptyList())

    Log.d("todoentityState", "MyScreen: $todoEntitiesState")

    // 주간 성취도 계산
    val weeklyAchievement = calculateWeeklyAchievement(todoEntitiesState)
    val stringweeklyachievement = weeklyAchievement.toString()

    // 성취도에 따른 메시지
    val encouragementMessage = if (weeklyAchievement >= 50) {
        "잘하고 있어요! 👍"
    } else {
        "조금 더 노력해봐요! 💪"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth()
                        .scale(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "주간 성취도",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 30.dp)
                    )

                    CircularProgressIndicator(
                        progress = weeklyAchievement / 100f,
                        modifier = Modifier
                            .size(150.dp)
                            .padding(bottom = 8.dp),
                        color = if (weeklyAchievement >= 50) Color.Green else Color.Red,
                        strokeWidth = 8.dp,
                        trackColor = ProgressIndicatorDefaults.circularTrackColor,
                    )

                    LinearProgressBar(progress = weeklyAchievement/100f, modifier = Modifier.padding(top = 30.dp))
//
//                    CircularProgressBarWithText(
//                        progress = 0.75f, // Example progress value
//                        progressText = {weeklyAchievement}.toString(),
//                        modifier = Modifier.size(200.dp)
//                    )


                    Text(
                        text = "${String.format("%.1f", weeklyAchievement)}%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            }

            Text(
                text = encouragementMessage,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 20.dp),
                fontWeight = FontWeight.Bold,
                color = if (weeklyAchievement >= 50) Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.height(10.dp))
            
            Button(onClick = {
                GlobalScope.launch(Dispatchers.IO) {
                    fixedRepository.deleteAll()
                }
            }) {
                Text(text = "고정 스케줄 초기화")
            }
        }
    }
}

// 주간 성취도 계산 함수
fun calculateWeeklyAchievement(todoEntitiesState: List<TodoEntity>): Float {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    // 현재 날짜를 기준으로 해당 주의 시작일(월요일)과 종료일(일요일)을 계산
    calendar.time = Date()
    calendar.add(Calendar.DAY_OF_WEEK, -calendar.get(Calendar.DAY_OF_WEEK) + 2) // 해당 주의 월요일
    val startOfWeek = calendar.get(Calendar.YEAR) * 10000 +
            (calendar.get(Calendar.MONTH) + 1) * 100 +
            calendar.get(Calendar.DAY_OF_MONTH)

    calendar.add(Calendar.DAY_OF_WEEK, 6) // 해당 주의 일요일
    val endOfWeek = calendar.get(Calendar.YEAR) * 10000 +
            (calendar.get(Calendar.MONTH) + 1) * 100 +
            calendar.get(Calendar.DAY_OF_MONTH)

    val weekTodoEntities = todoEntitiesState.filter { it.deadline in startOfWeek..endOfWeek }
    val passweekTodoEntities = todoEntitiesState.filter { it.deadline in startOfWeek..endOfWeek && it.completed }

    val weekTodoCount = weekTodoEntities.size.toFloat()
    val passweekTodoCount = passweekTodoEntities.size.toFloat()

    // 주간 성취도 계산 및 반환
    return if (weekTodoCount > 0) {
        passweekTodoCount / weekTodoCount * 100
    } else {
        0f // 주간 작업이 없는 경우 성취도는 0%
    }
}

@Composable
fun LinearProgressBar(progress: Float, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Color.Gray, shape = RoundedCornerShape(5.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(10.dp)
                .background(Color.Green, shape = RoundedCornerShape(5.dp))
        )
    }
}


//@Composable
//fun CircularProgressBarWithText(
//    progress: Float,
//    modifier: Modifier = Modifier,
//    progressBarColor: Color = Color.Green,
//    progressBarBackgroundColor: Color = Color.Gray,
//    strokeWidth: Dp = 8.dp,
//    progressText: String
//) {
//    Box(modifier = modifier) {
//        Canvas(
//            modifier = Modifier.size(150.dp)
//        ) {
//            val outerRadius = size.minDimension / 2f
//            val innerRadius = outerRadius - strokeWidth.toPx()
//
//            val center = Offset(size.width / 2f, size.height / 2f)
//
//            // Draw background circle
//            drawCircle(
//                color = progressBarBackgroundColor,
//                radius = outerRadius,
//                center = center,
//                style = Stroke(width = strokeWidth.toPx())
//            )
//
//            // Draw progress arc
//            val startAngle = 270f
//            val sweepAngle = 360 * progress
//            drawArc(
//                color = progressBarColor,
//                startAngle = startAngle,
//                sweepAngle = sweepAngle,
//                useCenter = false,
//                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
//            )
//            // Draw progress text
//            val textStyle = TextStyle(
//                color = Color.Black,
//                fontSize = 40.sp
//            )
//            drawIntoCanvas { canvas ->
//                canvas.nativeCanvas.drawText(
//                    progressText,
//                    center.x,
//                    center.y + textStyle.fontSize.toPx() / 3, // Adjust vertical alignment
//                    textStyle.toPaint()
//                )
//            }
//        }
//    }
//}

@Composable
fun CircularProgressBarWithText(
    progress: Float,
    modifier: Modifier = Modifier,
    progressBarColor: Color = Color.Green,
    progressBarBackgroundColor: Color = Color.LightGray,
    strokeWidth: Float = 8f,
    progressText: String
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val outerRadius = size.minDimension / 2f
            val innerRadius = outerRadius - strokeWidth

            val center = Offset(size.width / 2f, size.height / 2f)

            // Draw background circle
            drawCircle(
                color = progressBarBackgroundColor,
                radius = outerRadius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Draw progress arc
            drawArc(
                color = progressBarColor,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                size = Size(outerRadius * 2, outerRadius * 2),
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius)
            )

            // Draw progress text
            drawIntoCanvas { canvas ->
                val textPaint = android.graphics.Paint().apply {
                    color = Color.Black.toArgb()
                    textSize = 40f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val textOffset = android.graphics.Rect().apply {
                    textPaint.getTextBounds(progressText, 0, progressText.length, this)
                }
                val x = center.x - textOffset.exactCenterX()
                val y = center.y - textOffset.exactCenterY()
                canvas.nativeCanvas.drawText(progressText, x, y, textPaint)
            }
        }
        Text(
            text = progressText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Green
        )
    }
}